package meelesh.authorizationService.controller;

import lombok.extern.slf4j.Slf4j;
import meelesh.authorizationService.dto.TokenRefreshDTO;
import meelesh.authorizationService.dto.request.LoginRequestDTO;
import meelesh.authorizationService.dto.request.RegisterRequestDTO;
import meelesh.authorizationService.entity.AuthUser;
import meelesh.authorizationService.service.MainUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/api/auth")
public class AuthController {

    MainUserService mainUserService;

    @Autowired
    public AuthController(MainUserService mainUserService) {
        this.mainUserService = mainUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO registerRequestDTO) {
        AuthUser authUser = new AuthUser(registerRequestDTO.getUsername(), registerRequestDTO.getPassword(), registerRequestDTO.getEmail());
        return mainUserService.createUser(authUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        return mainUserService.loginUser(loginRequestDTO);
    }

    @GetMapping("/delete/{password}")
    // задать jwt токен, чтобы получить User's Principal
    public ResponseEntity<String> delete(@AuthenticationPrincipal AuthUser user, @PathVariable String password) {
        return mainUserService.deleteUser(user, password);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<String> token(@RequestBody TokenRefreshDTO tokenDTO) {
        return mainUserService.refreshToken(tokenDTO);
    }

}
