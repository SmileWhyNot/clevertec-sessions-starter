package vlad.kuchuk.dto;

import java.time.LocalDateTime;

public record UserResponse(Long id, String login, String password, String name, LocalDateTime sessionOpeningTime) {
}
