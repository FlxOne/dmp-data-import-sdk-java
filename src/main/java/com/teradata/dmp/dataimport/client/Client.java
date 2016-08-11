package com.teradata.dmp.dataimport.client;

import com.teradata.dmp.dataimport.config.IConfig;

/**
 * Client
 *
 * @author Teradata
 */
public class Client extends AbstractClient implements IClient {

    public Client(IConfig config) {
        super(config);
    }
}
