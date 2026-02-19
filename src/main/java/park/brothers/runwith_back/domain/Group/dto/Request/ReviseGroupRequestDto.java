package park.brothers.runwith_back.domain.Group.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class ReviseGroupRequestDto {

    @NotNull
    private Long groupId;

    @NotNull //Long에서는 NotNull을 사용해야 한다.
    private Long runnerId;

    private int certificationCriteria;

    private String description;

    private String imageLink;
}
