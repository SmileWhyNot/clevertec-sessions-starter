package vlad.kuchuk.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import vlad.kuchuk.model.AuthInfo;
import vlad.kuchuk.validator.UserValidation;

import java.time.LocalDateTime;

@UserValidation
public record UserRequest(@NotNull @NotBlank String login,
                          @NotNull @NotBlank String password,
                          @NotNull @NotBlank String name,
                          @JsonIgnore LocalDateTime sessionOpeningTime) implements AuthInfo {
}