package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vlad.kuchuk.dto.SessionMapper;
import vlad.kuchuk.dto.SessionRequest;
import vlad.kuchuk.dto.SessionResponse;
import vlad.kuchuk.exception.SessionCreationException;
import vlad.kuchuk.repository.SessionRepository;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    @Transactional
    public SessionResponse findSessionByLogin(String login) {
        return sessionRepository.findByLogin(login)
                .map(sessionMapper::toDto)
                .orElseGet(() -> createSession(login));
    }

    private SessionResponse createSession(String login) {
        SessionRequest request = new SessionRequest(login);
        return Optional.of(request)
                       .map(sessionMapper::toEntity)
                       .map(sessionRepository::save)
                       .map(sessionMapper::toDto)
                       .orElseThrow(() -> new SessionCreationException("Failed to create session"));
    }

    @Transactional
    public void deleteAllExpiredSessions(LocalDateTime curDateTime) {
        sessionRepository.deleteSessionByOpeningTimeBefore(curDateTime);
    }
}