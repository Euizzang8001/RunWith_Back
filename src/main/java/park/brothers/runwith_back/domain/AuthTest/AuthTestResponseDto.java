package park.brothers.runwith_back.domain.AuthTest;

import lombok.Data;

@Data
public class AuthTestResponseDto {
    private String idToken;      // 우리가 필요한 그 토큰!
    private String email;
    private String refreshToken; // 토큰 만료 시 갱신용
    private String expiresIn;    // 유효 시간 (초)
    private String localId;      // 사용자의 UID
    private boolean registered;
}
