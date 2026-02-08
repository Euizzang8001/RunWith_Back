package park.brothers.runwith_back.domain.Runner.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateRunnerRequestDto {
    @NotEmpty
    private String name;

    @NotEmpty
    private String email;

    @NotEmpty
    private String password;

    private String imageLink;
}
