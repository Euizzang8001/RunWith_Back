package park.brothers.runwith_back.domain.Belong.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LeaveGroupRequestDto {

    @NotNull
    Long runnerId;

    @NotNull
    Long groupId;

}

