package com.nineteens.storage;

import com.nineteens.common.exception.BadRequestException;
import com.nineteens.common.exception.NotFoundException;
import com.nineteens.config.AppProperties;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalStorageService implements StorageService {

    private static final Logger log = LoggerFactory.getLogger(LocalStorageService.class);
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    private final AppProperties properties;
    private Path root;

    public LocalStorageService(AppProperties properties) {
        this.properties = properties;
    }

    @PostConstruct
    void init() throws IOException {
        root = Paths.get(properties.getStorage().getLocalDir()).toAbsolutePath().normalize();
        Files.createDirectories(root);
        log.info("Local file storage initialized at {}", root);
    }

    @Override
    public StoredFile store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Image file is required");
        }
        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "image" : file.getOriginalFilename());
        String extension = extensionOf(original);
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BadRequestException("Unsupported image type. Use jpg, png, webp, or gif");
        }
        String filename = UUID.randomUUID() + "." + extension;
        Path destination = root.resolve(filename);
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, destination, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new IllegalStateException("Failed to store file", ex);
        }
        String publicUrl = properties.getStorage().getPublicPrefix() + "/" + filename;
        return new StoredFile(filename, publicUrl, file.getContentType(), file.getSize());
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = resolve(filename);
            Resource resource = new UrlResource(file.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                throw new NotFoundException("File not found");
            }
            return resource;
        } catch (MalformedURLException ex) {
            throw new NotFoundException("File not found");
        }
    }

    @Override
    public Path resolve(String filename) {
        Path resolved = root.resolve(filename).normalize();
        if (!resolved.startsWith(root)) {
            throw new BadRequestException("Invalid file path");
        }
        return resolved;
    }

    private static String extensionOf(String filename) {
        int dot = filename.lastIndexOf('.');
        if (dot < 0) {
            return "";
        }
        return filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}
