package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);
    Optional<User> findByEmail(String email);
}
