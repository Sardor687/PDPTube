package uz.app.pdptube.controller;


import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.app.pdptube.dto.VideoDTO;
import uz.app.pdptube.entity.Video;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.VideoRepository;
import uz.app.pdptube.service.VideoService;
import uz.app.pdptube.service.VideoStorageService;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;
    private final VideoStorageService videoStorageService;
    private final VideoRepository videoRepository;

    @PostMapping
    public ResponseEntity<?> postVideo(@RequestBody VideoDTO videoDTO) {
        ResponseMessage serviceResponse = videoService.postVideo(videoDTO);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }

    //moved to other controller
   /* @PostMapping(value = "/upload/{videoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload a video file to S3")
    public ResponseEntity<String> uploadVideo(@PathVariable Integer videoId ,@RequestParam("file") MultipartFile file) throws IOException {
        // Ensure the file is a valid video format (optional, based on your requirement)
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No video file provided");
        }

        // Call the service to upload the video file to S3 or another storage service
        String videoUrl = videoStorageService.storeVideo(file);
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        Video video = optionalVideo.get();
        video.setVideoLink(videoUrl);
        // Return the URL of the uploaded video
        return ResponseEntity.ok(videoUrl);
    }*/




    @PostMapping("/like/{videoId}")
    public ResponseEntity<?> likeVideo(@PathVariable Integer videoId) {
        ResponseMessage serviceResponse = videoService.likeVideo(videoId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.ACCEPTED : HttpStatus.NOT_FOUND).body(serviceResponse);
    }

    @PostMapping("/dislike/{videoId}")
    public ResponseEntity<?> dislikeVideo(@PathVariable Integer videoId) {
        ResponseMessage serviceResponse = videoService.dislikeVideo(videoId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.ACCEPTED : HttpStatus.NOT_FOUND).body(serviceResponse);
    }

    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable Integer videoId) {
        ResponseMessage serviceResponse = videoService.deleteVideo(videoId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchVideos(@RequestParam String title) {
        ResponseMessage responseMessage = videoService.searchVideos(title);
        return ResponseEntity.status(responseMessage.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND)
                .body(responseMessage);
    }
}
