package park.brothers.runwith_back.domain.Belong.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetBelongOfGroupResponseDto {

    @NotEmpty
    private String belongId; //belong id

    @NotEmpty
    private String groupId; //그룹 id

    @NotEmpty
    private String groupName; //그룹 이름

    @NotEmpty
    private String groupDescription; //그룹 설명

    private String groupImageLink; //그룹의 이미지 링크

}
