package com.shree.web.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {

    public Map<String,String> storeFile(MultipartFile file,String location)throws IOException;
    
    public Resource loadFileAsResource(String fileId,String fileName)throws IOException;
}
