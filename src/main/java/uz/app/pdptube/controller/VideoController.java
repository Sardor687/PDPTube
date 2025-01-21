package uz.app.pdptube.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.pdptube.dto.VideoDTO;
import uz.app.pdptube.entity.Video;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.service.VideoService;

@RestController
@RequestMapping("/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping
    public ResponseEntity<?> postVideo(@RequestBody VideoDTO videoDTO) {
        ResponseMessage serviceResponse = videoService.postVideo(videoDTO);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }

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


}
