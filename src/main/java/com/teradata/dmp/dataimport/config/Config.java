package com.teradata.dmp.dataimport.config;

public class Config extends AbstractConfig implements IConfig {

    public static IConfig getDefault() {
        IConfig config = new Config();
        config.setHost("go.flx1.com");
        config.setScheme("https");
        return config;
    }
}
