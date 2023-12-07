package vlad.kuchuk.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import vlad.kuchuk.annotation.SessionManager;
import vlad.kuchuk.dto.UserRequest;
import vlad.kuchuk.dto.UserResponse;
import vlad.kuchuk.model.AuthInfoImpl;
import vlad.kuchuk.model.Session;
import vlad.kuchuk.service.UserService;

@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @SessionManager(blackList = {"John", "Trello"})
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/{id}")
    public UserResponse findById(@RequestParam(value = "login", required = false) AuthInfoImpl authInfo,
                                 @PathVariable Long id,
                                 Session session) {
        log.info("findById method called with session = " + session.toString());
        log.info("findById method UserRequest authInfo = " + authInfo);
        return userService.findById(id);
    }

    @SessionManager(blackList = {"Alice", "Nata"})
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserResponse save(@RequestBody @Valid UserRequest request, Session session) {
        log.info("save method called with session = " + session.toString());
        log.info("save method UserRequest request = " + request);
        return userService.save(new UserRequest(request.login(), request.password(), request.name()
                , session.openingTime()));
    }
}