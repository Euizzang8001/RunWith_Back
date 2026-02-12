package park.brothers.runwith_back.domain.Belong.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChangeLeaderRequestDto {

    @NotNull
    Long beforeLeaderId;

    @NotNull
    Long afterLeaderId;

    @NotNull
    Long groupId;
}
