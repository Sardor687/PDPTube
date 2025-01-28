package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.Channel;
import uz.app.pdptube.entity.Video;
import uz.app.pdptube.enums.Category;

import java.util.List;


public interface VideoRepository extends JpaRepository<Video, Integer> {
   void deleteAllByChannel(Channel channel);
   List<Video> findByChannel(Channel channel);
   List<Video> findByCategory(Category category);
   List<Video> findByTitleContainingIgnoreCase(String title);
}
