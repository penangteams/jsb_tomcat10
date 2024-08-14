package com.example.springbootsampleprojectdeployonexternalserver.storage;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Setter
@Getter
@Service
@ConfigurationProperties("storage")

public class StorageProperties {

    /**
     * Folder location for storing files
     */
    private String location = "upload-dir";

}