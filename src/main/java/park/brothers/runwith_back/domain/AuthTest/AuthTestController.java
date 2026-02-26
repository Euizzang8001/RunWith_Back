package park.brothers.runwith_back.domain.AuthTest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RestController
@RequestMapping("/public")
@RequiredArgsConstructor
public class AuthTestController {

    private final AuthTestService authTestService;

    @PostMapping("/auth/login")
    public ResponseEntity<AuthTestResponseDto> login(@RequestBody AuthTestRequestDto loginRequest) {
        // 서비스 호출하여 토큰 받아오기
        AuthTestResponseDto tokenResponse = authTestService.loginWithEmail(loginRequest);

        // 결과 반환 (idToken이 포함되어 있음)
        return ResponseEntity.ok(tokenResponse);
    }
}