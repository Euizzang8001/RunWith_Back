package park.brothers.runwith_back.domain.Belong.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class GetBelongOfRunnerResponseDto {

    @NotEmpty
    private String belongId;

    @NotEmpty
    private String runnerId;

    @NotEmpty
    private String runnerName;

    private boolean isLeader; //리더 여부

    private String runnerImageLink;

}
