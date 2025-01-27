package uz.app.pdptube.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.app.pdptube.entity.Playlist;
import uz.app.pdptube.entity.PlaylistVideos;
import uz.app.pdptube.entity.Video;
import uz.app.pdptube.helper.Helper;
import uz.app.pdptube.payload.ResponseMessage;
import uz.app.pdptube.repository.PlaylistRepository;
import uz.app.pdptube.repository.PlaylistVideosRepository;
import uz.app.pdptube.repository.VideoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlaylistService {
    private final PlaylistRepository playlistRepository;

    private final PlaylistVideosRepository playlistVideosRepository;
    private final VideoRepository videoRepository;

    public ResponseMessage getPlaylists(){
        List<Playlist> playlists = playlistRepository.findByOwner(Helper.getCurrentPrincipal().getId());
        if (playlists.isEmpty()) {
            return new ResponseMessage(false, "you don't have playlists", playlists);
        }else {
            return new ResponseMessage(true, "Here are your playlists", playlists);
        }
    }
    public ResponseMessage getPlaylistVideos(Integer playlistId) {
        Optional<Playlist> byId = playlistRepository.findById(playlistId);
        if (byId.isPresent()) {
            List<PlaylistVideos> playlistVideos = playlistVideosRepository.findByPlaylist(playlistId);
            if (playlistVideos.isEmpty()) {
                return new ResponseMessage(false, "you don't have videos in this playlist", playlistVideos);
            }else {
                //pv -> pv.getVideo(); changed
                List<Integer> videosId = playlistVideos.stream().map(PlaylistVideos::getVideo).toList();
                List<Video> videos = new ArrayList<>();
                for (Integer id : videosId) {
                    Optional<Video> optionalVideo = videoRepository.findById(id);
                    if (optionalVideo.isPresent()) {
                        videos.add(optionalVideo.get());
                    }else {
                        return new ResponseMessage(false, "playlistVideo relation bor, video yo'q , backendda xato, videoId :" + id, null);
                    }
                }
                return new ResponseMessage(true, "Here are the videos in this playlist", videos);
            }



        }else {
            return new ResponseMessage(false, "playlist with this id doesn't exist", playlistId);
        }
    }

    public ResponseMessage addPlaylist(String title) {
        List<Playlist> playlists = playlistRepository.findByOwner(Helper.getCurrentPrincipal().getId());
        for (Playlist playlist : playlists) {
            if (playlist.getTitle().equals(title)) {
                return new ResponseMessage(false, "playlist with this title already exists", playlist.getId());
            }
        }
        Playlist playlist = new Playlist();
        playlist.setTitle(title);
        playlist.setOwner(Helper.getCurrentPrincipal().getId());
        playlistRepository.save(playlist);
        return new ResponseMessage(true, "Playlist added", playlist.getId());
    }
    @Transactional
    public ResponseMessage deletePlaylist(Integer playlistId) {
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(playlistId);
        if (optionalPlaylist.isPresent()) {
            Playlist playlist = optionalPlaylist.get();
            if (playlist.getOwner().equals(Helper.getCurrentPrincipal().getId())) {
                playlistRepository.delete(playlist);
                playlistVideosRepository.deleteAllByPlaylist(playlistId);
                return new ResponseMessage(true, "Playlist deleted", playlist.getId());
            }else {
                return new ResponseMessage(false, "this playlist isn't yours", playlist.getId());
            }
        }else {
            return new ResponseMessage(false, "playlist with this id doesn't exist", playlistId);
        }
    }


    public ResponseMessage updatePlaylist(Integer playlistId, String newTitle) {
        List<Playlist> playlists = playlistRepository.findByOwner(Helper.getCurrentPrincipal().getId());

        if (playlists.isEmpty()) {
            return new ResponseMessage(false, "you don't have playlists", playlists);
        }else {
            for (Playlist playlist : playlists) {
                if (playlist.getId().equals(playlistId)) {
                    for (Playlist checkerPlaylist : playlists) {
                        if (checkerPlaylist.getTitle().equals(newTitle)) {
                            return new ResponseMessage(false, "playlist with this title already exists", playlist.getId());
                        }
                    }
                    playlist.setTitle(newTitle);
                    playlistRepository.save(playlist);
                    return new ResponseMessage(true, "Playlist updated", playlist.getId());
                }
            }
            return new ResponseMessage(false, "Playlist with this id doesn't exist", playlistId);
        }
    }
     private boolean fileUploaded(String videoLink){
        return !(videoLink.equalsIgnoreCase("String") || videoLink.isEmpty() || videoLink.isBlank());
    }
    public ResponseMessage addVideoToPlaylist(Integer playlistId, Integer videoId) {
        Optional<Video> optionalVideo = videoRepository.findById(videoId);
        if (optionalVideo.isPresent()) {
            Optional<Playlist> optionalPlaylist = playlistRepository.findById(playlistId);
            if (optionalPlaylist.isPresent()) {
                Playlist playlist = optionalPlaylist.get();
                if (playlist.getOwner().equals(Helper.getCurrentPrincipal().getId())) {
                    Video video = optionalVideo.get();
                    if (!fileUploaded(video.getVideoLink())) {
                        return new ResponseMessage(false, "video with this idea doesn't have a file yet, so it is not active", video.getVideoLink());
                    }
                    if (playlistVideosRepository.existsByPlaylistAndVideo(playlistId, videoId)) {
                        return new ResponseMessage(false, "video with this idea already exists in playlist  " + playlistId, videoId);
                    }
                    PlaylistVideos playlistVideos = new PlaylistVideos();
                    playlistVideos.setPlaylist(playlistId);
                    playlistVideos.setVideo(videoId);
                    playlistVideosRepository.save(playlistVideos);
                    return new ResponseMessage(true, "video " + videoId + "added to playlist " + playlistId, null);
                }else {
                    return new ResponseMessage(false, "this playlist isn't yours", playlist.getId());
                }
            }else {
                return new ResponseMessage(false, "playlist with this id doesn't exist", playlistId);
            }
        }else {
            return new ResponseMessage(false, "video with this id doesn't exist", videoId);
        }
    }

    @Transactional
    public ResponseMessage removeVideoFromPlaylist(Integer playlistId, Integer videoId) {
        Optional<Playlist> optionalPlaylist = playlistRepository.findById(playlistId);
        if (optionalPlaylist.isPresent()) {
            Playlist playlist = optionalPlaylist.get();
            if (playlist.getOwner().equals(Helper.getCurrentPrincipal().getId())) {
                if (videoRepository.findById(videoId).isPresent()) {
                    List<PlaylistVideos> playlistVideosList = playlistVideosRepository.findByPlaylist(playlistId);
                    for (PlaylistVideos playlistVideos : playlistVideosList) {
                        if (playlistVideos.getVideo().equals(videoId)) {
                            playlistVideosRepository.delete(playlistVideos);
                            return new ResponseMessage(true, "video " + videoId + "removed from playlist " + playlistId, null);
                        }
                    }
                    return new ResponseMessage(false, "this video " + videoId + " isn't in this playlist", playlistId);
                }else {
                    return new ResponseMessage(false, "video with this id doesn't exist", videoId);
                }
            }else {
                return new ResponseMessage(false, "this playlist isn't yours", playlist.getId());
            }
        }else {
            return new ResponseMessage(false, "playlist with this id doesn't exist", playlistId);
        }
    }
}
