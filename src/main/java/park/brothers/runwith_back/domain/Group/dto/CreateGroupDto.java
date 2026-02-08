package park.brothers.runwith_back.domain.Group.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateGroupDto {
    @NotEmpty
    private String name;

    @NotNull //Long에서는 NotNull을 사용해야 한다.
    private Long runnerId;

    @NotEmpty
    private String nickname;
}
