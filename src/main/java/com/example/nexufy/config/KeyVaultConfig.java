package com.example.nexufy.config;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.security.keyvault.secrets.SecretClient;
import com.azure.security.keyvault.secrets.SecretClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeyVaultConfig {

    @Value("${azure.keyvault.uri}")
    private String keyVaultUri;

    private String storageConnectionString;

    public String getStorageConnectionString() {
        if (storageConnectionString == null) {
            SecretClient secretClient = new SecretClientBuilder()
                    .vaultUrl(keyVaultUri)
                    .credential(new DefaultAzureCredentialBuilder().build())
                    .buildClient();

            storageConnectionString = secretClient.getSecret("AzureStorageConnectionString").getValue();
        }
        return storageConnectionString;
    }
}
