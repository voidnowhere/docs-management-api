package fr.norsys.docmanagementapi.service;

import fr.norsys.docmanagementapi.entity.Doc;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Component
public class StorageService {
    private final String rootDest = "storage/docs";
    private final Path rootDestPath;

    public StorageService() throws IOException {
        this.rootDestPath = Paths.get(rootDest);
        Files.createDirectories(rootDestPath);
    }

    public String storeFile(UUID docId, MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String fileName = docId.toString() + file
                .getOriginalFilename()
                .substring(file.getOriginalFilename().lastIndexOf("."));
        Path destFile = this.rootDestPath
                .resolve(Paths.get(fileName))
                .normalize()
                .toAbsolutePath();

        if (!destFile.getParent().equals(this.rootDestPath.toAbsolutePath())) {
            // This is a security check
            throw new RuntimeException("Cannot store file outside current directory.");
        }

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, destFile, StandardCopyOption.REPLACE_EXISTING);
        }

        return rootDest + "/" + fileName;
    }

    public Doc fillDocInfo(MultipartFile file) {
        String title = file
                .getOriginalFilename()
                .substring(0, file.getOriginalFilename().lastIndexOf("."));
        String type = file.getContentType();

        return Doc
                .builder()
                .title(title)
                .type(type)
                .build();
    }

    public Resource getFileAsResource(String docPath) throws MalformedURLException {
        Path destFile = Paths.get(docPath);

        return new UrlResource(destFile.toUri());
    }

    public String getFileChecksum(String filePath) throws IOException {
        try (InputStream fileStream = Files.newInputStream(Paths.get(filePath))) {
            return DigestUtils.sha256Hex(fileStream);
        }
    }

    public String getFileChecksum(MultipartFile file) throws IOException {
        return DigestUtils.sha256Hex(file.getInputStream());
    }
}
