package park.brothers.runwith_back.domain.Runner.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CreateRunnerResponseDto {

    @NotEmpty
    private String runnerId;

    @NotEmpty
    private String runnerName;

    @NotEmpty
    private String runnerEmail;

    private String runnerImageLink;
}
