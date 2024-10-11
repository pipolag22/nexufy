package com.example.nexufy.service;

import com.azure.storage.blob.*;
import com.azure.storage.blob.models.BlobHttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AzureBlobService {

    private final BlobContainerClient containerClient;

    public AzureBlobService(
            @Value("${azure.storage.connection-string}") String connectionString,
            @Value("${azure.storage.container-name}") String containerName
    ) {
        BlobServiceClient serviceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();
        containerClient = serviceClient.getBlobContainerClient(containerName);
    }

    public String uploadFile(MultipartFile file, String productId) throws IOException {
        // Utiliza el productId en el nombre del blob para relacionar la imagen con el producto
        String blobFileName = "product-" + productId + "-" + System.currentTimeMillis() + "-" + file.getOriginalFilename();
        BlobClient blobClient = containerClient.getBlobClient(blobFileName);

        blobClient.upload(file.getInputStream(), file.getSize(), true);

        // Opcional: Establecer el tipo de contenido (MIME type)
        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());
        blobClient.setHttpHeaders(headers);

        // Devolver la URL del blob
        return blobClient.getBlobUrl();
    }
}
