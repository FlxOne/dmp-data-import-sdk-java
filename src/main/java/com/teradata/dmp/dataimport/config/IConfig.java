package com.teradata.dmp.dataimport.config;

/**
 * IConfig
 *
 * @author Teradata
 */
public interface IConfig {

    String getHost();

    String getScheme();

    void setHost(String host);

    void setScheme(String scheme);
}
