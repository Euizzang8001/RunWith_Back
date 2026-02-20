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
    private String groupName;

    @NotEmpty
    private String runnerId;

    @NotEmpty
    private String groupNickname;

    @NotNull
    private int groupCertificationCriteria;

    @NotEmpty
    private String groupDescription;
}
