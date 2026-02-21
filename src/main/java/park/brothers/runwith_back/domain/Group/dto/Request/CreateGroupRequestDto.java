package park.brothers.runwith_back.domain.Group.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CreateGroupRequestDto {
    @NotEmpty
    private String groupName; //그룹 이름

    @NotEmpty
    private String runnerId; //그룹을 생성하는 러너 id

    @NotEmpty
    private String groupNickname; //생성하는 러너가 그룹 내에서 사용할 닉네임

    @NotNull
    private int groupCertificationCriteria; //그룹 인정 기준

    @NotEmpty
    private String groupDescription; //그룹 설명
}
