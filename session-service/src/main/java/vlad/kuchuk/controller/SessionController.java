package vlad.kuchuk.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vlad.kuchuk.dto.SessionRequest;
import vlad.kuchuk.dto.SessionResponse;
import vlad.kuchuk.service.SessionService;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/sessions")
public class SessionController {

    private final SessionService sessionService;
    // 2. В сервисе должны быть методы создания и получения текущей сессии по логину
    // 3. В сессии должен быть айдишник, логин, которому принадлежит сессия и время открытия сессии

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public SessionResponse findSessionOrCreateIfNotExist(@RequestBody SessionRequest sessionRequest) {
        return sessionService.findSession(sessionRequest);
    }
}