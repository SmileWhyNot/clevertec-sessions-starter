package vlad.kuchuk.controller;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vlad.kuchuk.dto.SessionResponse;
import vlad.kuchuk.service.SessionService;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public SessionResponse findSessionOrCreateIfNotExist(@Valid @RequestParam("login") String login) {
        return sessionService.findSessionByLogin(login);
    }
}
