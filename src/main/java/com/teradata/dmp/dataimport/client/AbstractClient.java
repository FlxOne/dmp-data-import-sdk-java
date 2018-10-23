package com.teradata.dmp.dataimport.client;

import com.google.gson.Gson;
import com.teradata.dmp.dataimport.Dimensions;

import com.teradata.dmp.dataimport.config.IConfig;
import com.teradata.dmp.dataimport.request.IRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.client.utils.URIBuilder;
import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClientConfig;
import org.asynchttpclient.DefaultAsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClientConfig;
import org.asynchttpclient.Response;

/**
 * Abstract Client
 *
 * @author Teradata
 */
public abstract class AbstractClient implements IClient {

    private final URIBuilder builder = new URIBuilder();
    private final DefaultAsyncHttpClient asyncHttpClient;
    private final AtomicLong runningCounter = new AtomicLong();
    private final String host;
    private final Random random;
    private final CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();

    public AbstractClient(IConfig config) {
        this.host = config.getHost();
        this.random = new Random();

        builder.setScheme(config.getScheme());

        DefaultAsyncHttpClientConfig.Builder configBuilder = new DefaultAsyncHttpClientConfig.Builder();
        configBuilder.setKeepAlive(true);

        AsyncHttpClientConfig asyncHttpClientConfig = configBuilder.build();
        this.asyncHttpClient = new DefaultAsyncHttpClient(asyncHttpClientConfig);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                future.complete(getHostAddresses());
            }
        }, 0, 1000);
    }

    @Override
    public ArrayList<String> getHostAddresses() {
        ArrayList<String> hostAddresses = new ArrayList<>();

        try {
            for (InetAddress addr : InetAddress.getAllByName(host)) {
                hostAddresses.add(addr.getHostAddress());
            }
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        return hostAddresses;
    }

    @Override
    public void execute(IRequest request) {
        future.whenComplete((hostAddresses, unusedButRequired) -> {
            if (request.getAttempts() >= 3) {
                System.out.println("Max attempts reached");
                return;
            }

            request.addAttempt();

            try {
                long running = runningCounter.incrementAndGet();
                if (running > 100) {
                    try {
                        synchronized (runningCounter) {
                            runningCounter.wait();
                        }
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }

                // Set path
                builder.setPath(request.getPath());

                // Clear
                builder.clearParameters();

                // Round-robin
                builder.setHost(hostAddresses.get(random.nextInt(hostAddresses.size())));

                request.set(Dimensions.EXTERNAL_DATA, new Gson().toJson(request.getCustomData()));
                request.getDefaults().entrySet().stream().forEach((entry) -> {
                    builder.addParameter(entry.getKey(), entry.getValue().toString());
                });

                System.out.println(builder.toString());

                asyncHttpClient.prepareGet(builder.build().toString()).execute(new AsyncCompletionHandler<Response>() {
                    @Override
                    public Response onCompleted(Response response) throws Exception {
                        synchronized (runningCounter) {
                            runningCounter.decrementAndGet();
                            runningCounter.notify();
                        }

                        return response;
                    }

                    @Override
                    public void onThrowable(Throwable t) {
                        // Retry
                        execute(request);

                        System.out.println("onThrowable: " + t.getMessage());
                    }

                });
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        });
    }

}
