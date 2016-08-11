package com.teradata.dmp.dataimport;

import com.google.gson.Gson;
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
 * Client
 *
 * @author Teradata
 */
public class Client {

    private final URIBuilder builder = new URIBuilder();
    private final DefaultAsyncHttpClient asyncHttpClient;
    private final AtomicLong runningCounter = new AtomicLong();
    private final String host;
    private final Random random;
    private final CompletableFuture<ArrayList<String>> future = new CompletableFuture<>();

    public Client(String host) {
        this.host = host;
        this.random = new Random();

        DefaultAsyncHttpClientConfig.Builder configBuilder = new DefaultAsyncHttpClientConfig.Builder();
        configBuilder.setKeepAlive(true);
        configBuilder.setAcceptAnyCertificate(true);

        AsyncHttpClientConfig asyncHttpClientConfig = configBuilder.build();
        this.asyncHttpClient = new DefaultAsyncHttpClient(asyncHttpClientConfig);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                future.complete(getHostAddresses());
            }
        }, 0, 1000);
    }

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

    public void setScheme(String scheme) {
        builder.setScheme(scheme);
    }

    public void setPath(String path) {
        builder.setPath(path);
    }

    public void execute(Request request) {
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
