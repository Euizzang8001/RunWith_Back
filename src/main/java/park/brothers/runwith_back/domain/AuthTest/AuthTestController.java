package park.brothers.runwith_back.domain.AuthTest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
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
    @Operation(summary = "토큰 생성 테스트용 api",description = "이메일 로그인을 기반으로 토큰을 조회합니다.")
    public ResponseEntity<AuthTestResponseDto> login(
            @Parameter(
                    description = "테스트할 로그인 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestBody AuthTestRequestDto loginRequest
    ) {
        // 서비스 호출하여 토큰 받아오기
        AuthTestResponseDto tokenResponse = authTestService.loginWithEmail(loginRequest);

        // 결과 반환 (idToken이 포함되어 있음)
        return ResponseEntity.ok(tokenResponse);
    }
}