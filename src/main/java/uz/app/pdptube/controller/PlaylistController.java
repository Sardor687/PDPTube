package uz.app.pdptube.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.service.PlaylistService;

@RestController
@RequestMapping("/playlist")
@RequiredArgsConstructor
public class PlaylistController {

    private final PlaylistService playlistService;



    @GetMapping
    public ResponseEntity<ResponseMessage> getPlaylists() {
        ResponseMessage serviceResponse = playlistService.getPlaylists();
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(serviceResponse);
    }


    @GetMapping("/videos/{playlistId}")
    public ResponseEntity<ResponseMessage> getPlaylistVideos(@PathVariable Integer playlistId) {
        ResponseMessage serviceResponse = playlistService.getPlaylistVideos(playlistId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.NOT_FOUND).body(serviceResponse);
    }


















    @PostMapping("/add")
    public ResponseEntity<ResponseMessage> addPlaylist(@RequestParam String title) {
        ResponseMessage serviceResponse = playlistService.addPlaylist(title);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }



    @DeleteMapping("/delete/{playlistId}")
    public ResponseEntity<ResponseMessage> deletePlaylist(@PathVariable Integer playlistId) {
        ResponseMessage serviceResponse = playlistService.deletePlaylist(playlistId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }

    @PutMapping("/update/{playlistId}")
    public ResponseEntity<ResponseMessage> updatePlaylist(@PathVariable Integer playlistId, @RequestParam String newTitle) {
        ResponseMessage serviceResponse = playlistService.updatePlaylist(playlistId, newTitle);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }

    @PostMapping("/videos/add")
    public ResponseEntity<ResponseMessage> addVideoToPlaylist(@RequestParam Integer playlistId, @RequestParam Integer videoId) {
        ResponseMessage serviceResponse = playlistService.addVideoToPlaylist(playlistId, videoId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }

    @DeleteMapping("/videos/remove")
    public ResponseEntity<ResponseMessage> removeVideoFromPlaylist(@RequestParam Integer playlistId, @RequestParam Integer videoId) {
        ResponseMessage serviceResponse = playlistService.removeVideoFromPlaylist(playlistId, videoId);
        return ResponseEntity.status(serviceResponse.success() ? HttpStatus.OK : HttpStatus.BAD_REQUEST).body(serviceResponse);
    }
}

