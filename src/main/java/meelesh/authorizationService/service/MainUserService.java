package meelesh.authorizationService.service;

import lombok.extern.slf4j.Slf4j;
import meelesh.authorizationService.dto.StatusDTO;
import meelesh.authorizationService.dto.TokenRefreshDTO;
import meelesh.authorizationService.dto.request.LoginRequestDTO;
import meelesh.authorizationService.dto.response.DeleteUserResponseDTO;
import meelesh.authorizationService.dto.response.LoginResponseDTO;
import meelesh.authorizationService.dto.response.RegisterResponseDTO;
import meelesh.authorizationService.entity.AuthUser;
import meelesh.authorizationService.entity.CustomUserDetails;
import meelesh.authorizationService.repository.UserRepository;
import meelesh.authorizationService.security.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.BearerTokenAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
public class MainUserService {

    private final UserRepository userRepository;
    private final UserManager userDetailsManager;
    private final TokenGenerator tokenGenerator;
    private final DaoAuthenticationProvider daoAuthenticationProvider;
    private final PasswordEncoder passwordEncoder;
    private JwtAuthenticationProvider refreshTokenAuthProvider;


    @Autowired
    public MainUserService(UserManager userManager,
                           TokenGenerator tokenGenerator,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           DaoAuthenticationProvider daoAuthenticationProvider) {
        this.userDetailsManager = userManager;
        this.tokenGenerator = tokenGenerator;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<String> createUser(AuthUser authUser) {
        if (userRepository.existsByUsername(authUser.getUsername())) {
            StatusDTO status = new StatusDTO();
            status.setCode(409);
            status.setDescription("User already exists");
            return new ResponseEntity<>(status.toJson(), HttpStatus.CONFLICT);
        }

        userDetailsManager.createUser(authUser);
        Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(authUser, authUser.getPassword(), Collections.EMPTY_LIST);
        RegisterResponseDTO response = new RegisterResponseDTO(String.valueOf(authUser.getId()), authUser.getUsername(), tokenGenerator.createToken(authentication));

//        registerInBudgetService(user, response);

        return new ResponseEntity<>(response.toJson(), HttpStatus.OK);
    }

    public ResponseEntity<String> loginUser(LoginRequestDTO loginDTO) {
        Authentication authentication;
        try {
            authentication = daoAuthenticationProvider.authenticate(UsernamePasswordAuthenticationToken.unauthenticated(loginDTO.getUsername(), loginDTO.getPassword()));
        } catch (Exception e) {
            log.info(e.toString());
            return new ResponseEntity<>(new StatusDTO(403, "Unauthorized").toJson(), HttpStatus.UNAUTHORIZED);
        }
        CustomUserDetails details = (CustomUserDetails) authentication.getPrincipal();
        return new ResponseEntity<>(new LoginResponseDTO(String.valueOf(details.getId()), details.getUsername(), tokenGenerator.createToken(authentication)).toJson(), HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<String> deleteUser(AuthUser user, String password) {
        if (user == null)
            return new ResponseEntity<>(new StatusDTO(404, "user not found").toJson(), HttpStatus.NOT_FOUND);
        Optional<AuthUser> userRepo = userRepository.findById(user.getId());
        if (userRepo.isPresent()) {
            AuthUser userRepoGet = userRepo.get();
            System.out.println(passwordEncoder.encode(password));
            if (passwordEncoder.matches(password, userRepoGet.getPassword())) {
                userRepository.deleteById(userRepoGet.getId());
                return ResponseEntity.ok(new DeleteUserResponseDTO(true).toJson());
            }
        }
        return new ResponseEntity<>(new StatusDTO(401, "unauthorized").toJson(), HttpStatus.UNAUTHORIZED);
    }

    public ResponseEntity<String> refreshToken(TokenRefreshDTO tokenDTO) {
        Authentication authentication = refreshTokenAuthProvider.authenticate(new BearerTokenAuthenticationToken(tokenDTO.getRefreshToken()));
        Jwt jwt = (Jwt) authentication.getCredentials();
        long id = Long.parseLong((String) jwt.getClaims().get("sub"));
        if (userRepository.existsById(Long.parseLong(tokenDTO.getUserId())) && tokenDTO.getUserId().equals(Long.toString(id)))
            return ResponseEntity.ok(tokenGenerator.createToken(authentication).toJson());
        return new ResponseEntity<>(new StatusDTO(404, "user not found").toJson(), HttpStatus.NOT_FOUND);
    }

    @Autowired
    @Qualifier("jwtRefreshTokenAuthProvider")
    public void setRefreshTokenAuthProvider(JwtAuthenticationProvider refreshTokenAuthProvider) {
        this.refreshTokenAuthProvider = refreshTokenAuthProvider;
    }

}
