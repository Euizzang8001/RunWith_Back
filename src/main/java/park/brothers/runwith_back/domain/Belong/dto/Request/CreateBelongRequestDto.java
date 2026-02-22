package park.brothers.runwith_back.domain.Belong.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateBelongRequestDto {

    @NotEmpty
    private String runnerId; // 그룹에 가입하려는 러너 id

    @NotEmpty
    private String groupId; // 가입하려는 그룹 id

    @NotEmpty
    String belongNickname; // 그룹에서 사용할 닉네임
}
