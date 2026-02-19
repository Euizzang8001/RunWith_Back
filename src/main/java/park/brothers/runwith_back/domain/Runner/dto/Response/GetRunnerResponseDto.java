package park.brothers.runwith_back.domain.Runner.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRunnerResponseDto {

    @NotNull
    Long runnerId;

    @NotEmpty
    String name;

    String imageLink;
}
