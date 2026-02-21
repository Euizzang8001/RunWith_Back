package park.brothers.runwith_back.domain.Login.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestDto {

    @NotEmpty
    private String loginEmail; //로그인 이메일

    @NotEmpty
    private String loginPassword; //로그인 비밀번호
}
