package me.projects.gatekeeper.api_management_service.Controller;

import jakarta.validation.Valid;
import me.projects.gatekeeper.api_management_service.Service.AuthenticationService;
import me.projects.gatekeeper.api_management_service.dto.Request.LoginRequest;
import me.projects.gatekeeper.api_management_service.dto.Response.LoginResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        return ResponseEntity.ok(authenticationService.login(loginRequest));
    }
}
