package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.UserLikedVideos;

public interface UserLikedVideosRepository extends JpaRepository<UserLikedVideos, Integer> {
    boolean existsByOwnerAndVideo(Integer userId, Integer videoId);
}
