package uz.app.pdptube.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service class for managing video storage operations using Amazon S3.
 * Handles uploading, retrieving, and deleting videos.
 * Implements secure file handling and proper error management.
 *
 * @version 1.0
 * @since 2025-01-17
 */
@Service
public class VideoStorageService {
    private static final Logger logger = LoggerFactory.getLogger(VideoStorageService.class);
    private static final String VIDEO_PREFIX = "video/";
    private static final long MAX_FILE_SIZE = 500 * 1024 * 1024; // 250MB (videos are generally larger)
    private static final String[] ALLOWED_CONTENT_TYPES = {
            "video/mp4", "video/webm", "video/ogg"
    };

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public VideoStorageService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Uploads a video to Amazon S3.
     * Generates a unique filename and validates file type and size.
     *
     * @param file Video file to upload
     * @return URL of the uploaded video
     * @throws IllegalArgumentException if file is invalid
     * @throws IOException              if file processing fails
     */
    public String storeVideo(MultipartFile file) throws IOException {
        validateFile(file);

        String filename = generateUniqueFilename(file);
        String key = VIDEO_PREFIX + filename;

        try {
            logger.info("Uploading video: {}", filename);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            s3Client.putObject(bucketName, key, file.getInputStream(), metadata);
            String videoUrl = s3Client.getUrl(bucketName, key).toString();

            logger.info("Successfully uploaded video: {}", filename);
            return videoUrl;
        } catch (AmazonServiceException e) {
            logger.error("Failed to upload video: {}", e.getMessage());
            throw new IOException("Failed to upload video to S3", e);
        }
    }

    /**
     * Deletes a video from Amazon S3.
     *
     * @param videoUrl URL of the video to delete
     * @throws IllegalArgumentException if URL is invalid
     */
    public void deleteVideo(String videoUrl) {
        try {
            String key = extractKeyFromUrl(videoUrl);
            logger.info("Deleting video with key: {}", key);

            s3Client.deleteObject(bucketName, key);
            logger.info("Successfully deleted video: {}", key);
        } catch (AmazonServiceException e) {
            logger.error("Failed to delete video: {}", e.getMessage());
            throw new IllegalStateException("Failed to delete video from S3", e);
        }
    }

    /**
     * Validates video file properties including size and content type.
     * Ensures only video files are uploaded.
     *
     * @param file File to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.error("Empty file provided");
            throw new IllegalArgumentException("Hey, you can't upload nothing! We need an actual video here! 🎥");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            logger.error("File size exceeds limit: {} bytes", file.getSize());
            throw new IllegalArgumentException(
                    "Whoa there! That video is too large (over 250MB)! Compress it a bit! 🏋️‍♂️");
        }

        String contentType = file.getContentType();
        if (contentType == null || contentType.equals("application/octet-stream")) {
            String fileName = file.getOriginalFilename();
            if (fileName != null) {
                String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                switch (extension) {
                    case "mp4":
                        contentType = "video/mp4";
                        break;
                    case "webm":
                        contentType = "video/webm";
                        break;
                    case "ogg":
                        contentType = "video/ogg";
                        break;
                    default:
                        logger.error("Invalid file extension: {}", extension);
                        throw new IllegalArgumentException(
                                "Sorry, this file type isn't supported! Only MP4, WebM, and OGG are allowed! 🎬");
                }
            }
        }

        boolean isValidContentType = false;
        for (String allowedType : ALLOWED_CONTENT_TYPES) {
            if (allowedType.equals(contentType)) {
                isValidContentType = true;
                break;
            }
        }

        if (!isValidContentType) {
            logger.error("Invalid content type: {}", contentType);
            throw new IllegalArgumentException(
                    "Nice try, but we only accept real videos here! MP4, WebM, or OGG - pick your fighter! 🥊");
        }
    }

    /**
     * Generates a unique filename for the uploaded file.
     *
     * @param file Original file
     * @return Generated unique filename
     */
    private String generateUniqueFilename(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

    /**
     * Extracts the S3 key from a full URL.
     *
     * @param url Full S3 URL
     * @return Extracted key
     * @throws IllegalArgumentException if URL is invalid
     */
    private String extractKeyFromUrl(String url) {
        try {
            String[] parts = url.split(bucketName + "/");
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid S3 URL format");
            }
            return parts[1];
        } catch (Exception e) {
            logger.error("Failed to extract key from URL: {}", url);
            throw new IllegalArgumentException("Invalid S3 URL", e);
        }
    }
}
