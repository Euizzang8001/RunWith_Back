package park.brothers.runwith_back.domain.Group.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class ReviseGroupRequestDto {

    private int groupCertificationCriteria; //수정하려는 그룹 인정 기준

    private String groupDescription; //수정하려는 그룹 설명
}
