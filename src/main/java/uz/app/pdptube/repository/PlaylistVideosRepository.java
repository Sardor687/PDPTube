package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.PlaylistVideos;

import java.util.List;

public interface PlaylistVideosRepository extends JpaRepository<PlaylistVideos, Integer> {
    void deleteAllByPlaylist(Integer playlistId);

    List<PlaylistVideos> findByPlaylist(Integer playlistId);

    void deleteByVideo(Integer videoId);

    boolean existsByPlaylistAndVideo(Integer playlistId, Integer videoId);
}
