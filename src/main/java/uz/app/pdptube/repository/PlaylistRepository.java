package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.Playlist;

import java.util.List;

public interface PlaylistRepository extends JpaRepository<Playlist, Integer> {
    List<Playlist> findByOwner(Integer userId);
}
