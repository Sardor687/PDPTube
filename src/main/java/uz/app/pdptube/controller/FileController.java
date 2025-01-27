package uz.app.pdptube.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.app.pdptube.entity.Video;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.VideoRepository;
import uz.app.pdptube.service.VideoService;
import uz.app.pdptube.service.VideoStorageService;

import java.io.IOException;

/**
 * Controller for video storage operations via multipart file upload.
 */
@RestController
@RequestMapping("/files")
public class FileController {
    private final VideoService videoService;

    private final VideoStorageService videoStorageService;
    private final VideoRepository videoRepository;

    @Autowired
    public FileController(VideoService videoService, VideoStorageService videoStorageService, VideoRepository videoRepository) {
        this.videoService = videoService;
        this.videoStorageService = videoStorageService;
        this.videoRepository = videoRepository;
    }

    /**
     * Uploads a video file to Amazon S3.
     *
     * @param file Video file to upload
     * @return Response with the URL of the uploaded video
     * @throws IOException if file upload fails
     */
    @PostMapping(value = "/upload/{videoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a video file to S3")
    public ResponseEntity<String> uploadVideo(@RequestParam("file") MultipartFile file, @PathVariable Integer videoId) throws IOException {
        // Ensure the file is a valid video format (optional, based on your requirement)
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No video file provided");
        }
        if (videoId == null) {
            return ResponseEntity.badRequest().body("No video id provided");
        }
        ResponseMessage serviceResponse = videoService.getVideo(videoId);
        if (serviceResponse.success()){
            Video video = (Video) serviceResponse.data();
            System.out.println(video);
            if (Helper.getCurrentPrincipal().getId().equals(video.getId())) {
                if (video.getVideoLink().equalsIgnoreCase("String") || video.getVideoLink().isEmpty() || video.getVideoLink().isBlank()) {
                    String videoUrl = videoStorageService.storeVideo(file);
                    video.setVideoLink(videoUrl);
                    videoRepository.save(video);
                    return ResponseEntity.status(HttpStatus.CREATED).body(video.getVideoLink());
                }else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("this video already has a file:  "+ video.getVideoLink());
                }
            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("This video isn't yours , so you can't upload the file!");
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(serviceResponse.toString());
        }
    }

    /**
     * Deletes a video from the storage (e.g., S3) by its URL.
     *
     * @param videoUrl URL of the video to delete
     * @return Response indicating success or failure
     */
    //When we delete a video , the url will be lost in database koyeb ,so this isn't important right now.
   /* @DeleteMapping("/delete")
    @Operation(summary = "Delete a video from storage by URL")
    public ResponseEntity<String> deleteVideo(@RequestParam("videoUrl") String videoUrl) {
        try {
            videoStorageService.deleteVideo(videoUrl);
            return ResponseEntity.ok("Video deleted successfully");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Video not found: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to delete video: " + e.getMessage());
        }
    }*/
}
