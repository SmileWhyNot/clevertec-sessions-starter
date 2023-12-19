package vlad.kuchuk.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import vlad.kuchuk.dto.UserRequest;
import vlad.kuchuk.dto.UserResponse;
import vlad.kuchuk.service.UserService;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UserValidator implements ConstraintValidator<UserValidation, UserRequest> {

    private final UserService userService;

    @Override
    public void initialize(UserValidation constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(UserRequest value, ConstraintValidatorContext context) {
        Optional<UserResponse> userByLogin = userService.findUserByLogin(value.login());
        context.disableDefaultConstraintViolation();
        context
                .buildConstraintViolationWithTemplate("User with login = " + value.login() + " already created")
                .addConstraintViolation();
        return userByLogin.isEmpty();
    }
}