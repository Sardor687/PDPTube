package uz.app.pdptube.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.app.pdptube.entity.HistoryVideos;

import java.util.List;

public interface HistoryVideosRepository extends JpaRepository<HistoryVideos, Integer> {
    List<HistoryVideos> findAllByHistory(Integer historyId);
    void deleteByHistory(Integer historyId);
}
