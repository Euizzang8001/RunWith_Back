package park.brothers.runwith_back.domain.Runner.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CreateRunnerResponseDto {

    @NotNull
    private Long id;

    @NotEmpty
    private String name;

    @NotEmpty
    private String email;

    private String imageLink;
}
