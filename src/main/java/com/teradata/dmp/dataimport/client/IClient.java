package com.teradata.dmp.dataimport.client;

import com.teradata.dmp.dataimport.request.IRequest;
import java.util.ArrayList;

/**
 * IClient
 *
 * @author Teradata
 */
public interface IClient {

    ArrayList<String> getHostAddresses();

    void execute(IRequest request);
}
