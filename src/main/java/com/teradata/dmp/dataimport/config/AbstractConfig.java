package com.teradata.dmp.dataimport.config;

/**
 * Abstract Config
 *
 * @author Teradata
 */
public class AbstractConfig implements IConfig {

    protected String host;
    protected String scheme;

    @Override
    public String getHost() {
        return this.host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public String getScheme() {
        return this.scheme;
    }

    @Override
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

}
