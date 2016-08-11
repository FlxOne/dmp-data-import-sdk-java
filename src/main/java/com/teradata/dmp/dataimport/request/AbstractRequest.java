package com.teradata.dmp.dataimport.request;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.teradata.dmp.dataimport.Dimensions;
import java.util.HashMap;

/**
 * Abstract Request
 *
 * @author Teradata
 */
public class AbstractRequest implements IRequest {

    protected final HashMap<String, Object> defaults = new HashMap<>();
    protected final HashMap<String, Object> customData = new HashMap<>();
    protected int attempts = 0;
    protected String path;

    public AbstractRequest(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public int getAttempts() {
        return attempts;
    }

    @Override
    public void addAttempt() {
        attempts++;
    }

    @Override
    public void set(String key, String value) {
        defaults.put(key, value);
    }

    @Override
    public void remove(String key) {
        defaults.remove(key);
    }

    @Override
    public void removeCustomData(String key) {
        customData.remove(key);
    }

    @Override
    public void setCustomData(String key, JsonObject jsonObject) {
        customData.put(key, jsonObject);
    }

    @Override
    public void setCustomData(String key, JsonArray jsonArray) {
        customData.put(key, jsonArray);
    }

    @Override
    public void setCustomData(String key, JsonElement jsonElement) {
        customData.put(key, jsonElement);
    }

    @Override
    public void setCustomData(String key, int integer) {
        customData.put(key, integer);
    }

    @Override
    public void setCustomData(String key, String string) {
        customData.put(key, string);
    }

    @Override
    public void setCustomData(String key, boolean bool) {
        customData.put(key, bool);
    }

    @Override
    public void setCustomData(String key, long lon) {
        customData.put(key, lon);
    }

    @Override
    public HashMap<String, Object> getCustomData() {
        return customData;
    }

    @Override
    public void clearCustomData() {
        customData.clear();
    }

    @Override
    public HashMap<String, Object> getDefaults() {
        return defaults;
    }

    @Override
    public void clearDefaults() {
        defaults.clear();
    }

    @Override
    public void setCustomerId(int customerId) {
        defaults.put(Dimensions.FLXONE_CUSTOMER_ID, customerId);
    }

    @Override
    public void setPixelId(int pixelId) {
        defaults.put(Dimensions.PIXEL_ID, pixelId);
    }
}
