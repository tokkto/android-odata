package com.colusbisd.fonninez.logon;

import com.sap.cloud.mobile.foundation.authentication.BasicAuthCredentialStore;
import com.sap.cloud.mobile.foundation.securestore.SecureKeyValueStore;

import java.util.HashMap;
import java.util.Map;

/**
 * This class implements a {@link BasicAuthCredentialStore}, where the credentials are
 * persisted in the applications secure store. The implementation of the logon flow guaranties,
 * that the secure store is already present, when the credential store needs it.
 */

@SuppressWarnings("unchecked")
public class BasicAuthPersistentCredentialStore implements BasicAuthCredentialStore {

    private static final String CRED_KEY = "basicauth_credentials";

    private final SecureStoreManager secureStoreManager;

    public BasicAuthPersistentCredentialStore(SecureStoreManager secureStoreManager) {
        this.secureStoreManager = secureStoreManager;
    }

    @Override
    public synchronized void storeCredential(String rootUrl, String realm, String[] credentials) {
        secureStoreManager.doWithApplicationStore(applicationStore -> {
            Map<String, String[]> credMap = applicationStore.getSerializable(CRED_KEY);
            if (credMap == null) {
                credMap = new HashMap<>();
            }
            credMap.put(this.makeKey(rootUrl, realm), credentials);
            applicationStore.put(CRED_KEY, credMap);
        });
    }

    @Override
    public synchronized String[] getCredential(String rootUrl, String realm) {
        String[] retVal = null;
        if(secureStoreManager.isApplicationStoreOpen()) {
            Map<String, String[]> credMap =
                    secureStoreManager.getWithApplicationStore(applicationStore -> applicationStore.getSerializable(CRED_KEY));
            if (credMap != null) {
                retVal = credMap.get(makeKey(rootUrl, realm));
            }
        }
        return retVal;
    }

    @Override
    public synchronized void deleteCredential(String rootUrl, String realm) {
        if(secureStoreManager.isApplicationStoreOpen()) {
            secureStoreManager.doWithApplicationStore(applicationStore -> {
                Map<String, String[]> credMap = applicationStore.getSerializable(CRED_KEY);
                if (credMap != null) {
                    credMap.remove(makeKey(rootUrl, realm));
                }
                applicationStore.put(CRED_KEY, credMap);
            });
        }
    }

    @Override
    public synchronized void deleteAllCredentials() {
        if(secureStoreManager.isApplicationStoreOpen()) {
            secureStoreManager.doWithApplicationStore(applicationStore -> {
                applicationStore.remove(CRED_KEY);
            });
        }
    }

    private String makeKey(String rootUrl, String realm) {
        return rootUrl + "::" + realm;
    }
}

