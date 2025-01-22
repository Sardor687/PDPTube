package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.ChannelOwner;

import java.util.Optional;

public interface ChannelOwnerRepository extends JpaRepository<ChannelOwner, Integer> {
    Optional<ChannelOwner> findByOwner(Integer ownerId);
    Optional<ChannelOwner> findByChannel(Integer channelId);

}
