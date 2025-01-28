package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.UserDislikedComments;

import java.util.Optional;

public interface UserDislikedCommentsRepository extends JpaRepository<UserDislikedComments, Integer> {
    Optional<UserDislikedComments> findByComment(Integer commentId);
    void deleteAllByComment(Integer commentId);
}

