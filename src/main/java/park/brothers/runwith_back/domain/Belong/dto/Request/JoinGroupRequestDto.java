package park.brothers.runwith_back.domain.Belong.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class JoinGroupRequestDto {

    @NotNull
    Long runnerId;

    @NotNull
    Long groupId;

    @NotEmpty
    String nickname;

    @NotEmpty
    Boolean isLeader;
}
