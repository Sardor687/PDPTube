package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.Channel;

import java.util.Optional;

public interface ChannelRepository extends JpaRepository<Channel, Integer> {
    Optional<Channel> findByOwnerId(Integer ownerId);
    void deleteById(Integer channelId);
}
