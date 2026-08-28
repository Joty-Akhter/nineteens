package com.nineteens.storage;

import java.nio.file.Path;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

    StoredFile store(MultipartFile file);

    Resource loadAsResource(String filename);

    Path resolve(String filename);

    record StoredFile(String filename, String publicUrl, String contentType, long size) {
    }
}
