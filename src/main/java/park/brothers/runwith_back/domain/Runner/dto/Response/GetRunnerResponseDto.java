package park.brothers.runwith_back.domain.Runner.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRunnerResponseDto {

    @NotEmpty
    Long id;

    @NotEmpty
    String name;

    String imageLink;
}
