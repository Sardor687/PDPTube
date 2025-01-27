package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.History;
import uz.app.pdptube.entity.User;

import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Integer> {
    Optional<History> findByOwner(User owner);
}
