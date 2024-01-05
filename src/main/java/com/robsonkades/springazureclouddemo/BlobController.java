package com.robsonkades.springazureclouddemo;

import com.azure.spring.cloud.core.resource.AzureStorageBlobProtocolResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.WritableResource;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("blob")
public class BlobController {

    static final String BLOB_RESOURCE_PATTERN = "azure-blob://%s/%s";

    private static final Logger logger = LoggerFactory.getLogger(BlobController.class);
    private final AzureStorageBlobProtocolResolver azureStorageBlobProtocolResolver;

    public BlobController(
            AzureStorageBlobProtocolResolver patternResolver) {
        this.azureStorageBlobProtocolResolver = patternResolver;
    }

    @GetMapping("/{containerName}")
    public List<String> listTxtFiles(@PathVariable String containerName) throws IOException {
        Resource[] resources = azureStorageBlobProtocolResolver.getResources(String.format(BLOB_RESOURCE_PATTERN, containerName, "*.txt"));
        logger.info("{} resources founded with pattern:*.txt", resources.length);
        return Stream.of(resources).map(Resource::getFilename).collect(Collectors.toList());
    }

    @GetMapping("/{containerName}/{fileName}")
    public String readResource(@PathVariable("fileName") String fileName, @PathVariable String containerName) throws IOException {
        Resource resource = azureStorageBlobProtocolResolver.getResource(String.format(BLOB_RESOURCE_PATTERN, containerName, fileName));
        return StreamUtils.copyToString(resource.getInputStream(), Charset.defaultCharset());
    }

    @PostMapping("/{containerName}/{fileName}")
    public String writeResource(@PathVariable("fileName") String fileName, @RequestBody String data, @PathVariable String containerName) throws IOException {
        Resource resource = azureStorageBlobProtocolResolver.getResource(String.format(BLOB_RESOURCE_PATTERN, containerName, fileName));
        try (OutputStream os = ((WritableResource) resource).getOutputStream()) {
            os.write(data.getBytes());
        }
        return "blob was updated";
    }
}
