package br.com.freitas.lockapp.web;

import br.com.freitas.lockapp.dto.RequestDto;
import br.com.freitas.lockapp.dto.ResponseDto;
import br.com.freitas.lockapp.dto.TokenDto;
import br.com.freitas.lockapp.sec.TokenService;
import br.com.freitas.lockapp.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    TokenService tokenService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserService userService;

    @PostMapping("/login")
    public ResponseEntity<ResponseDto<TokenDto>> login(@Valid @RequestBody RequestDto request) {
        logger.info("Attempting login for user: {}", request.username());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            TokenDto tokenDto = tokenService.generateToken(authentication);

            logger.info("Login successful for user: {}", request.username());

            return ResponseEntity.ok(new ResponseDto<>("Login successful", tokenDto));

        } catch (org.springframework.security.core.AuthenticationException e) {

            logger.error("Login failed for user: {}", request.username(), e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ResponseDto<>("Invalid username or password", null));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ResponseDto<Void>> signup(@Valid @RequestBody RequestDto request) {
        logger.info("Attempting to register new user: {}", request.username());
        try {
            userService.save(request);
            logger.info("New user registered: {}", request.username());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDto<>("User registered successfully", null));
        } catch (Exception e) {
            logger.error("Failed to register user: {}", request.username(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto<>("Failed to register user", null));
        }
    }

    @PutMapping("/update-password/{id}")
    public ResponseEntity<ResponseDto<Void>> updatePassword(@PathVariable @NotNull Long id, @RequestParam @NotNull String password) {
        logger.info("Attempting to update password for user id: {}", id);
        try {
            userService.updatePassword(id, password);
            logger.info("Password updated successfully for user id: {}", id);
            return ResponseEntity.ok(new ResponseDto<>("Password updated successfully", null));
        } catch (Exception e) {
            logger.error("Failed to update password for user id: {}", id, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ResponseDto<>("Failed to update password", null));
        }
    }
}
