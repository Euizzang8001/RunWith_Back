package park.brothers.runwith_back.domain.Runner.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetMyInfoResponseDto {

    @NotEmpty
    private String runnerName;

    private String runnerImageLink;
}
