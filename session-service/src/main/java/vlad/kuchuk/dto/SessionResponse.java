package vlad.kuchuk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * DTO for {@link vlad.kuchuk.entity.Session}
 */
public record SessionResponse(
        @NotNull Long id,
        @Size(min = 2, max = 100) @NotBlank String login,
        @NotNull @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime openingTime
) implements Serializable {
}