package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.UserDislikedVideos;


public interface UserDislikedVideosRepository extends JpaRepository<UserDislikedVideos, Integer> {
    boolean existsByOwnerAndVideo(Integer userId, Integer videoId);
    void deleteByVideo(Integer videoId);
    void deleteByOwnerAndVideo(Integer userId, Integer videoId);
}
