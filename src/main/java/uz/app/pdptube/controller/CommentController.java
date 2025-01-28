package uz.app.pdptube.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.pdptube.dto.CommentDTO;
import uz.app.pdptube.service.CommentService;
import uz.app.pdptube.payload.ResponseMessage;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    // Izoh qo'shish
    @PostMapping("/post/")
    public ResponseEntity<?> postComment(@RequestBody CommentDTO commentDTO) {
        ResponseMessage responseMessage = commentService.postComment(commentDTO);
        return ResponseEntity.status(responseMessage.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    // Izohni o'chirish
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Integer commentId) {
        ResponseMessage responseMessage = commentService.deleteComment(commentId);
        return ResponseEntity.status(responseMessage.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    // Video bo'yicha barcha izohlarni o'chirish (video egasi)
    @DeleteMapping("/deleteAll/{videoId}")
    public ResponseEntity<?> deleteAllCommentsByVideo(@PathVariable Integer videoId) {
        ResponseMessage responseMessage = commentService.deleteAllCommentsByVideo(videoId);
        return ResponseEntity.status(responseMessage.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }


    // Izohga like qo'shish
    @PostMapping("/like/{commentId}")
    public ResponseEntity<?> addLike(@PathVariable Integer commentId) {
        ResponseMessage responseMessage = commentService.addLike(commentId);
        return ResponseEntity.status(responseMessage.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    // Izohga dislike qo'shish
    @PostMapping("/dislike/{commentId}")
    public ResponseEntity<?> addDislike(@PathVariable Integer commentId) {
        ResponseMessage responseMessage = commentService.addDislike(commentId);
        return ResponseEntity.status(responseMessage.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST)
                .body(responseMessage);
    }

    @PostMapping("/reply")
    public ResponseEntity<?> addReply(@RequestBody CommentDTO commentDTO) {
        ResponseMessage serviceResponse = commentService.addReply(commentDTO);

    }
}
