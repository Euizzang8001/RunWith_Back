package park.brothers.runwith_back.domain.Belong.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ChangeLeaderRequestDto {

    @NotEmpty
    String beforeLeaderRunnerId;

    @NotEmpty
    String afterLeaderRunnerId;

    @NotEmpty
    String groupId;
}
