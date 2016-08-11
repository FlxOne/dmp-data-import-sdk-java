package com.teradata.dmp.dataimport.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;

/**
 * IRequest
 *
 * @author Teradata
 */
public interface IRequest {

    String getPath();

    void setPath(String path);

    int getAttempts();

    void addAttempt();

    void set(String key, String value);

    void remove(String key);

    void removeCustomData(String key);

    void setCustomData(String key, JsonObject jsonObject);

    void setCustomData(String key, JsonArray jsonArray);

    void setCustomData(String key, JsonElement jsonElement);

    void setCustomData(String key, int integer);

    void setCustomData(String key, String string);

    void setCustomData(String key, boolean bool);

    void setCustomData(String key, long lon);

    HashMap<String, Object> getCustomData();

    void clearCustomData();

    HashMap<String, Object> getDefaults();

    void clearDefaults();

    void setCustomerId(int customerId);

    void setPixelId(int pixelId);
}
