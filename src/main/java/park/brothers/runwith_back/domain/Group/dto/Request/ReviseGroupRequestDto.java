package park.brothers.runwith_back.domain.Group.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class ReviseGroupRequestDto {

    @NotEmpty
    private String groupId;

    @NotEmpty //Long에서는 NotNull을 사용해야 한다.
    private String runnerId;

    private int groupCertificationCriteria;

    private String groupDescription;

    private String groupImageLink;
}
