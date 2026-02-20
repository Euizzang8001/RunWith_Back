package park.brothers.runwith_back.domain.Runner.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CreateRunnerRequestDto {
    @NotEmpty
    private String runnerName;

    @NotEmpty
    private String runnerEmail;

    @NotEmpty
    private String runnerPassword;
}
