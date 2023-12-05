package vlad.kuchuk.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vlad.kuchuk.entity.Session;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByLogin(String login);

    void deleteSessionByOpeningTimeBefore(LocalDateTime currentTime);
}
