package park.brothers.runwith_back.domain.Belong.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateBelongResponseDto {

    @NotEmpty
    private String belongId; // 그룹에 가입한 belong id

    @NotEmpty
    private String groupId; //가입한 그룹 id

    @NotEmpty
    private String belongNickname; //러너가 이 그룹에서 사용하는 닉네임

    private Boolean belongIsLeader; //리더 여부
}
