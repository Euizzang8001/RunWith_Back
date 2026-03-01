package park.brothers.runwith_back.domain.Runner.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class ReviseMyInfoRequestDto {

    @NotEmpty
    private String runnerName;
}
