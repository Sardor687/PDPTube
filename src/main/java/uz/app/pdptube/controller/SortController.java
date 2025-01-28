package uz.app.pdptube.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.service.SortService;

@RestController
@RequestMapping("/sort")
@RequiredArgsConstructor
public class SortController {

    private final SortService sortService;

    @GetMapping("/your-videos")
    public ResponseEntity<ResponseMessage> getYourVideos() {
        ResponseMessage response = sortService.getYourVideosByUser();
        return ResponseEntity.status(response.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }

    @GetMapping("/{category}")
    public ResponseEntity<ResponseMessage> getVideosByCategory(@PathVariable String category) {
        ResponseMessage response = sortService.getVideosByCategory(category);
        return ResponseEntity.status(response.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(response);
    }
}
