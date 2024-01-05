package com.robsonkades.springazureclouddemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.stereotype.Component;

import java.io.OutputStream;

//@Component
public class InitializerSample implements CommandLineRunner {

    static final String BLOB_RESOURCE_PATTERN = "azure-blob://%s/%s";
    private static final Logger logger = LoggerFactory.getLogger(InitializerSample.class);
    private final ResourceLoader resourceLoader;

    public InitializerSample(@Qualifier("azureStorageBlobProtocolResolver") ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("StorageApplication data initialization begin ...");
        for (int i = 0; i < 10; i++) {
            String fileName = "file" + i + ".txt";
            Resource storageBlobResource = resourceLoader.getResource(String.format(BLOB_RESOURCE_PATTERN, "test", fileName));
            try (OutputStream os = ((WritableResource) storageBlobResource).getOutputStream()) {
                String data = "data" + i;
                os.write(data.getBytes());
                logger.info("write data to container={}, fileName={}", "test", fileName);
            }
        }
        logger.info("StorageApplication data initialization end ...");
    }
}
