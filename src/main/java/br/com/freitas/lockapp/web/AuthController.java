package br.com.freitas.lockapp.web;

import br.com.freitas.lockapp.dto.RequestDto;
import br.com.freitas.lockapp.dto.TokenDto;
import br.com.freitas.lockapp.model.User;
import br.com.freitas.lockapp.sec.TokenService;
import br.com.freitas.lockapp.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private TokenService tokenService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody RequestDto request) {
        var credentials = new UsernamePasswordAuthenticationToken(request.email(), request.password());
        var authenticate = authenticationManager.authenticate(credentials);
        final var accessToken = tokenService.generateToken((User) authenticate.getPrincipal());
        LOG.info("Authenticating now: {}", ((User) authenticate.getPrincipal()).getEmail());
        return new ResponseEntity<>(accessToken, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody RequestDto request) {
        userService.save(request);
        LOG.info("New user registered now: {}", request.email());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/update-password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, String password) {
        userService.updatePassword(id, password);
        return ResponseEntity.ok().build();
    }
}
