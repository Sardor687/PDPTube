package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.Channel_Owner;

import java.util.Optional;

public interface ChannelOwnerRepository extends JpaRepository<Channel_Owner, Integer> {
    Optional<Channel_Owner> findByOwner(Integer ownerId);

}
