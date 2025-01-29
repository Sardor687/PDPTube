package uz.app.pdptube.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.app.pdptube.entity.Channel;
import uz.app.pdptube.entity.ChannelOwner;
import uz.app.pdptube.entity.Notification;
import uz.app.pdptube.entity.Video;
import uz.app.pdptube.enums.NotificationType;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.ChannelOwnerRepository;
import uz.app.pdptube.repository.NotificationRepository;
import uz.app.pdptube.repository.VideoRepository;
import uz.app.pdptube.service.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * Controller for video storage operations via multipart file upload.
 */
@RestController
@RequestMapping("/files")
public class FileController {
    private final VideoService videoService;

    private final VideoStorageService videoStorageService;
    private final VideoRepository videoRepository;
    private final ChannelOwnerRepository channelOwnerRepository;
    private final SubscriptionService subscriptionService;
    private final ChannelService channelService;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;

    @Autowired
    public FileController(VideoService videoService, VideoStorageService videoStorageService, VideoRepository videoRepository, ChannelOwnerRepository channelOwnerRepository, SubscriptionService subscriptionService, ChannelService channelService, NotificationService notificationService, NotificationRepository notificationRepository) {
        this.videoService = videoService;
        this.videoStorageService = videoStorageService;
        this.videoRepository = videoRepository;
        this.channelOwnerRepository = channelOwnerRepository;
        this.subscriptionService = subscriptionService;
        this.channelService = channelService;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
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
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (!optionalVideo.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No video found with id " + videoId);
        }
        Video video = optionalVideo.get();
            System.out.println(video);
        Optional<ChannelOwner> optionalChannel = channelOwnerRepository.findByChannel(video.getChannel().getId());
        boolean present = optionalChannel.isPresent();
        if (!present) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Channel bor lekin channelOwner relation yoq , backendda xato: " + video.getChannel());
        }
        ChannelOwner channelOwner = optionalChannel.get();
        if (Helper.getCurrentPrincipal().getId().equals(channelOwner.getOwner())) {
                if (video.getVideoLink().equalsIgnoreCase("String") || video.getVideoLink().isEmpty() || video.getVideoLink().isBlank()) {

                    String videoUrl = videoStorageService.storeVideo(file);
                    video.setVideoLink(videoUrl);
                    videoRepository.save(video);
                    Channel channel = video.getChannel();
                    List<Integer> allFollowers = channelService.getAllFollowers(channel.getId());
                      Notification notification = Notification.builder()
                            .type(NotificationType.NEW_VIDEO_POSTED)
                            .subjectId(video.getId())
                            .build();

                    for (Integer follower : allFollowers) {
                        notification.setUserId(follower);
                        notificationRepository.save(notification);
                        notification.setId(null);
                    }

                    return ResponseEntity.status(HttpStatus.CREATED).body(video.getVideoLink());
                }else {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("this video already has a file:  "+ video.getVideoLink());
                }
            }else{
                return ResponseEntity.status(HttpStatus.CONFLICT).body("This video isn't yours , so you can't upload the file!");
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
