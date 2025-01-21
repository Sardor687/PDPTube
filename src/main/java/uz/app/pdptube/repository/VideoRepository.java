package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.Channel;
import uz.app.pdptube.entity.Video;


public interface VideoRepository extends JpaRepository<Video, Integer> {
   void deleteByChannel(Channel channel);
}
