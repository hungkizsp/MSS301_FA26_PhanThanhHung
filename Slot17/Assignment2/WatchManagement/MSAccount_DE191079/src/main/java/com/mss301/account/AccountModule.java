package com.mss301.account;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.nio.charset.StandardCharsets;
import java.util.*;
import lombok.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Entity
@Table(name = "StoreAccounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
class StoreAccount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "AccountID")
  Integer accountId;

  @Column(name = "Username", nullable = false, length = 20)
  String username;

  @Column(name = "Email", nullable = false)
  String email;

  @Column(name = "Password", nullable = false)
  String password;

  @Column(name = "Role")
  Integer role;

  @Builder.Default
  @Column(name = "IsActive")
  Boolean isActive = true;
}

interface AccountRepository extends JpaRepository<StoreAccount, Integer> {
  Optional<StoreAccount> findByUsername(String username);
}

record LoginRequest(@NotBlank String username, @NotBlank String password) {}

record TokenRequest(@NotBlank String refreshToken) {}

record LoginResponse(
  String accessToken,
  String refreshToken,
  String tokenType,
  Integer role,
  String username
) {}

record ApiResponse<T>(boolean success, String message, T data) {
  static <T> ApiResponse<T> ok(T d, String m) {
    return new ApiResponse<>(true, m, d);
  }

  static ApiResponse<Void> error(String m) {
    return new ApiResponse<>(false, m, null);
  }
}

@Service
class JwtService {

  @org.springframework.beans.factory.annotation.Value("${jwt.secret}")
  String secret;

  @org.springframework.beans.factory.annotation.Value("${jwt.expiration}")
  long expiration;

  @org.springframework.beans.factory.annotation.Value(
    "${jwt.refresh-expiration}"
  )
  long refreshExpiration;

  String token(String subject, Integer role, String type, long ttl) {
    return Jwts.builder()
      .subject(subject)
      .claim("type", type)
      .claim("role", role)
      .expiration(new Date(System.currentTimeMillis() + ttl))
      .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
      .compact();
  }

  String access(StoreAccount a) {
    return token(a.accountId.toString(), a.role, "access", expiration);
  }

  String refresh(StoreAccount a) {
    return token(a.accountId.toString(), a.role, "refresh", refreshExpiration);
  }

  Claims parse(String t) {
    return Jwts.parser()
      .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
      .build()
      .parseSignedClaims(t)
      .getPayload();
  }
}

@Service
@RequiredArgsConstructor
class AuthService {

  final AccountRepository repo;
  final PasswordEncoder encoder;
  final JwtService jwt;

  LoginResponse login(LoginRequest r) {
    StoreAccount a = repo
      .findByUsername(r.username())
      .orElseThrow(() ->
        new ResponseStatusException(
          HttpStatus.UNAUTHORIZED,
          "Invalid username or password"
        )
      );
    if (!a.isActive) throw new ResponseStatusException(
      HttpStatus.FORBIDDEN,
      "Account is inactive"
    );
    if (
      !encoder.matches(r.password(), a.password)
    ) throw new ResponseStatusException(
      HttpStatus.UNAUTHORIZED,
      "Invalid username or password"
    );
    return response(a);
  }

  LoginResponse refresh(TokenRequest r) {
    try {
      Claims c = jwt.parse(r.refreshToken());
      if (!"refresh".equals(c.get("type"))) throw new Exception();
      return response(
        repo
          .findById(Integer.valueOf(c.getSubject()))
          .orElseThrow(() -> new Exception())
      );
    } catch (Exception e) {
      throw new ResponseStatusException(
        HttpStatus.UNAUTHORIZED,
        "Invalid or expired refresh token"
      );
    }
  }

  LoginResponse response(StoreAccount a) {
    return new LoginResponse(
      jwt.access(a),
      jwt.refresh(a),
      "Bearer",
      a.role,
      a.username
    );
  }
}

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
class AuthController {

  final AuthService auth;

  @PostMapping("/login")
  ResponseEntity<ApiResponse<LoginResponse>> login(
    @jakarta.validation.Valid @RequestBody LoginRequest r
  ) {
    return ResponseEntity.ok(ApiResponse.ok(auth.login(r), "Login successful"));
  }

  @PostMapping("/refresh")
  ResponseEntity<ApiResponse<LoginResponse>> refresh(
    @jakarta.validation.Valid @RequestBody TokenRequest r
  ) {
    return ResponseEntity.ok(
      ApiResponse.ok(auth.refresh(r), "Token refreshed")
    );
  }

  @PostMapping("/logout")
  ResponseEntity<ApiResponse<Void>> logout() {
    return ResponseEntity.ok(ApiResponse.ok(null, "Logged out successfully"));
  }
}

@Configuration
class SecurityConfig {

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityFilterChain security(HttpSecurity h) throws Exception {
    h.csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(a ->
      a.anyRequest().permitAll()
    );
    return h.build();
  }

  @Bean
  CommandLineRunner seed(AccountRepository r, PasswordEncoder e) {
    return x -> {
      if (r.count() == 0) r.saveAll(
        List.of(
          account("adminsys", "admin@drawingpen.com.vn", 1, true, e),
          account("staff02", "staff02@drawingpen.com.vn", 2, true, e),
          account("member01", "member01@drawingpen.com.vn", 3, true, e),
          account("member02", "member02@drawingpen.com.vn", 3, false, e),
          account("staff01", "staff01@drawingpen.com.vn", 2, false, e)
        )
      );
    };
  }

  StoreAccount account(
    String u,
    String m,
    int role,
    boolean active,
    PasswordEncoder e
  ) {
    return StoreAccount.builder()
      .username(u)
      .email(m)
      .password(e.encode("@1"))
      .role(role)
      .isActive(active)
      .build();
  }
}
