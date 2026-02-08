package park.brothers.runwith_back.domain.Belong.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class JoinGroupRequestDto {

    @NotEmpty
    Long runnerId;

    @NotEmpty
    Long groupId;

    @NotEmpty
    String nickname;

    @NotEmpty
    Boolean isLeader;
}
