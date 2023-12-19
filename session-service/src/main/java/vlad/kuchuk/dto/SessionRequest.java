package vlad.kuchuk.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.io.Serializable;

/**
 * DTO for {@link vlad.kuchuk.entity.Session}
 */
public record SessionRequest(
        @Size(min = 2, max = 100) @NotBlank String login
) implements Serializable {
}