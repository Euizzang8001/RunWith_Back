package park.brothers.runwith_back.domain.Runner.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CreateRunnerRequestDto {
    @NotEmpty
    private String runnerName; // 회원가입할 러너 이름

    @NotEmpty
    private String runnerEmail; //회원가입할 러너 이메일(로그인 시 사용)

    @NotEmpty
    private String runnerPassword; //회원가입할 러너 비밀번호(로그인 시 사용)
}
