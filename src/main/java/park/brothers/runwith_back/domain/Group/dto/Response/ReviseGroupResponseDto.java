package park.brothers.runwith_back.domain.Group.dto.Response;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class ReviseGroupResponseDto {
    //다음 2개는 수정할 수 없는 정보
    @NotEmpty
    private String groupId; //수정된 groupId

    @NotEmpty
    private String groupName; //수정된 그룹 이름

    //여기서부터는 수정 가능한 정보들
    @NotEmpty
    private String groupDescription; //수정된 그룹 설명

    private String groupImageLink; //수정된 이미지의 링크

    @NotNull
    private int groupCertificationCriteria; //수정된 인증 기준
}
