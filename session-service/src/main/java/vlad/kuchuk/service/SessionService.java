package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vlad.kuchuk.dto.SessionMapper;
import vlad.kuchuk.dto.SessionRequest;
import vlad.kuchuk.dto.SessionResponse;
import vlad.kuchuk.exception.SessionCreationException;
import vlad.kuchuk.repository.SessionRepository;

import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SessionService {

    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;

    @Transactional
    public SessionResponse findSession(SessionRequest request) {
        return sessionRepository.findByLogin(request.login())
                .map(sessionMapper::toDto)
                .orElseGet(() -> createSession(request));
    }

    private SessionResponse createSession(SessionRequest request) {
        return Stream.of(request)
                .map(sessionMapper::toEntity)
                .map(sessionRepository::save)
                .map(sessionMapper::toDto)
                .findFirst()
                .orElseThrow(() -> new SessionCreationException("Failed to create session"));
    }
}
