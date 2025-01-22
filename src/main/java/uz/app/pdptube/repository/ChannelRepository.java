package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.Channel;


public interface ChannelRepository extends JpaRepository<Channel, Integer> {
}
