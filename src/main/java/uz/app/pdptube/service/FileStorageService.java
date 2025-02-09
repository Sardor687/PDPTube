package uz.app.pdptube.service;


import com.amazonaws.services.s3.AmazonS3;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileStorageService {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public FileStorageService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Uploads a file to S3 and returns the file URL.
     *
     * @param file the file to upload
     * @return the URL of the uploaded file
     * @throws IOException if file upload fails
     */
    public String uploadFile(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        try (InputStream inputStream = file.getInputStream()) {
            s3Client.putObject(bucketName, fileName, inputStream, null);
        }

        return s3Client.getUrl(bucketName, fileName).toString();
    }
}
