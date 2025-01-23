package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {
    Optional<Subscription> findByChannelAndFollower(Integer channelId, Integer followerId);
    boolean existsByChannelAndFollower(Integer channelId, Integer followerId);
    List<Subscription> findAllByChannel(Integer channelId);
    List<Subscription> findAllByFollower(Integer followerId);
}
