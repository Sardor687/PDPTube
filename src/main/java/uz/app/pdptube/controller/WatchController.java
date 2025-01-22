package uz.app.pdptube.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.service.CommentService;
import uz.app.pdptube.service.VideoService;

@RestController
@RequestMapping("/watch")
@RequiredArgsConstructor
public class WatchController {
    private final VideoService videoService;
    private final CommentService commentService;

    @GetMapping("/videos")
    public ResponseEntity<?> getVideos() {
        ResponseMessage serviceResponse = videoService.getVideos();
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(serviceResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getVideo(@PathVariable Integer id) {
        ResponseMessage serviceResponse = videoService.getVideo(id);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(serviceResponse);
    }



    @GetMapping("/video/comments/{videoId}")
    public ResponseEntity<?> getVideoComments(@PathVariable Integer videoId) {
        ResponseMessage serviceResponse = commentService.getVideoComments(videoId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(serviceResponse);
    }

}
