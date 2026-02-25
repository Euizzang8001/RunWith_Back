package park.brothers.runwith_back.domain.AuthTest;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthTestService {

    // application.properties 또는 yml에 설정된 API 키를 가져옴
    @Value("${firebase.web-api-key}")
    private String firebaseWebApiKey;

    public AuthTestResponseDto loginWithEmail(AuthTestRequestDto loginRequest) {
        // 1. 구글 로그인 API URL
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + firebaseWebApiKey;

        // 2. 요청 바디 설정 (이메일, 비밀번호, 토큰반환여부)
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("email", loginRequest.getEmail());
        requestBody.put("password", loginRequest.getPassword());
        requestBody.put("returnSecureToken", true); // 이게 있어야 토큰을 줍니다!

        // 3. REST API 호출
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<AuthTestResponseDto> response = restTemplate.postForEntity(
                    url,
                    requestBody,
                    AuthTestResponseDto.class
            );

            return response.getBody();

        } catch (HttpClientErrorException e) {
            // 로그인 실패 (비번 틀림, 없는 이메일 등) 처리
            throw new IllegalArgumentException("로그인 실패: 이메일이나 비밀번호를 확인하세요.");
        }
    }
}