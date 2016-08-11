package com.teradata.dmp.dataimport;

import com.google.gson.JsonObject;
import com.teradata.dmp.dataimport.client.Client;
import com.teradata.dmp.dataimport.config.Config;
import com.teradata.dmp.dataimport.config.IConfig;
import com.teradata.dmp.dataimport.request.IRequest;
import com.teradata.dmp.dataimport.request.Request;

/**
 * Example
 *
 * @author Teradata
 */
public class Example {

    public static void main(String args[]) {
        IConfig config = Config.getDefault();
        Client client = new Client(config);

        for (int i = 0; i < 10; i++) {
            IRequest request = new Request("/dp");

            // Default properties
            request.set(Dimensions.PIXEL_ID, "1");
            request.set(Dimensions.FLXONE_CUSTOMER_ID, "11");
            request.set(Dimensions.CAMPAIGN_ID, "123");

            // Custom properties
            request.setCustomData("gender", "male");

            // Custom property as json object
            JsonObject user = new JsonObject();
            user.addProperty("id", 1);
            user.addProperty("company", "Teradata");
            request.setCustomData("user", user);

            // Execute
            client.execute(request);
        }

    }

}
