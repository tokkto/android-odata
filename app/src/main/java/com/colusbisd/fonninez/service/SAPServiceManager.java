package com.colusbisd.fonninez.service;

import com.sap.cloud.android.odata.foninez.foninez;
import com.colusbisd.fonninez.app.ConfigurationData;
import com.sap.cloud.mobile.foundation.common.ClientProvider;
import com.sap.cloud.mobile.odata.OnlineODataProvider;
import com.sap.cloud.mobile.odata.core.Action0;
import com.sap.cloud.mobile.odata.http.OKHttpHandler;

public class SAPServiceManager {

    private final ConfigurationData configurationData;
    private OnlineODataProvider provider;
    private String serviceRoot;
    foninez foninez;
    public static final String CONNECTION_ID_FONINEZ = "FonnColDev";

    public SAPServiceManager(ConfigurationData configurationData) {
        this.configurationData = configurationData;
    }

    public void openODataStore(Action0 callback) {
        if (configurationData.loadData()) {
            String serviceUrl = configurationData.getServiceUrl();
            provider = new OnlineODataProvider("SAPService", serviceUrl + CONNECTION_ID_FONINEZ);
            provider.getNetworkOptions().setHttpHandler(new OKHttpHandler(ClientProvider.get()));
            provider.getServiceOptions().setCheckVersion(false);
            provider.getServiceOptions().setRequiresType(true);
            provider.getServiceOptions().setCacheMetadata(false);
            foninez = new foninez(provider);

        }
        callback.call();
    }

    public String getServiceRoot() {
        if (serviceRoot == null) {
            if (foninez == null) {
                throw new IllegalStateException("SAPServiceManager was not initialized");
            }
            provider = (OnlineODataProvider)foninez.getProvider();
            serviceRoot = provider.getServiceRoot();
        }
        return serviceRoot;
    }

    // This getter is used for the master-detail ui generation
    public foninez getfoninez() {
        if (foninez == null) {
            throw new IllegalStateException("SAPServiceManager was not initialized");
        }
        return foninez;
    }

}