package park.brothers.runwith_back.domain.Group.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CreateGroupResponseDto {

    @NotEmpty
    private String groupId; //생성된 그룹 id

    @NotEmpty
    private String groupName; //생성된 그룹 name

    @NotEmpty
    private String groupDescription; //생성된 그룹 설명

    private String groupImageLink; //이미지가 있었다면, 이미지 링크 보내기

    @NotEmpty
    private int groupCertificationCriteria; //생성된 그룹의 인증 조건
}
