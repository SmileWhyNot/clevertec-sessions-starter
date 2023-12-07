package vlad.kuchuk.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vlad.kuchuk.dto.UserMapper;
import vlad.kuchuk.dto.UserRequest;
import vlad.kuchuk.dto.UserResponse;
import vlad.kuchuk.exception.UserOperationException;
import vlad.kuchuk.repository.UserRepository;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserOperationException("Person with id %s is not found".formatted(id)));
    }

    public Optional<UserResponse> findUserByLogin(String login) {
        return userRepository.findByLogin(login)
                .map(userMapper::toDto);
    }

    @Transactional
    public UserResponse save(UserRequest request) {
        log.info("StarterUserService save method called. UserRequest request = " + request);
        return Optional.of(request)
                .map(userMapper::toEntity)
                .map(person -> {
                    try {
                        return userRepository.save(person);
                    } catch (DataIntegrityViolationException e) {
                        throw new UserOperationException("Person with login %s is already exist".formatted(request.login()));
                    }
                })
                .map(userMapper::toDto)
                .orElseThrow(() -> new UserOperationException("Can't save session"));
    }
}
