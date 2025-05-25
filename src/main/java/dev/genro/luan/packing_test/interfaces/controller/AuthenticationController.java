package dev.genro.luan.packing_test.interfaces.controller;

import dev.genro.luan.packing_test.configuration.security.JwtUtil;
import dev.genro.luan.packing_test.interfaces.dto.AuthenticationRequest;
import dev.genro.luan.packing_test.interfaces.dto.AuthenticationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class AuthenticationController {

  private final AuthenticationManager authenticationManager;
  private final UserDetailsService userDetailsService;
  private final JwtUtil jwtUtil;
  private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

  public AuthenticationController(AuthenticationManager authenticationManager,
                                  UserDetailsService userDetailsService,
                                  JwtUtil jwtUtil) {
    this.authenticationManager = authenticationManager;
    this.userDetailsService = userDetailsService;
    this.jwtUtil = jwtUtil;
  }

  @PostMapping("/authenticate")
  public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticationRequest authenticationRequest) {
    try {
      log.info("Attempting to authenticate user: {}", authenticationRequest.username());
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(authenticationRequest.username(), authenticationRequest.password())
      );
      log.info("User '{}' authenticated successfully by AuthenticationManager.", authenticationRequest.username());

      final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.username());
      log.info("Loaded UserDetails for '{}' for token generation. Authorities: {}", userDetails.getUsername(), userDetails.getAuthorities());

      final String jwt = jwtUtil.generateToken(userDetails);
      log.info("Generated JWT for user '{}'", userDetails.getUsername());

      return ResponseEntity.ok(new AuthenticationResponse(jwt));

    } catch (BadCredentialsException e) {
      log.warn("Authentication failed for user '{}': Incorrect username or password.", authenticationRequest.username());
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect username or password");
    } catch (Exception e) {
      log.error("An unexpected error occurred during token creation for user '{}'", authenticationRequest.username(), e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating token: %s".formatted(e.getMessage()));
    }
  }

}
