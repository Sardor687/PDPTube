package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.Comment;
import uz.app.pdptube.entity.Video;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    Optional<List<Comment>> findAllByVideoId(Integer videoId);

}
