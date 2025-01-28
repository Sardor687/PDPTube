package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.UserDislikedComments;
import uz.app.pdptube.entity.UserLikedComments;

import java.util.Optional;

public interface UserLikedCommentsRepository extends JpaRepository<UserLikedComments, Integer> {
    Optional<UserLikedComments> findByComment(Integer commentId);
    void deleteAllByComment(Integer commentId);
}
