package park.brothers.runwith_back.domain.Login.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {

    @NotEmpty
    private String runnerId; //러너ID

    @NotEmpty
    private String runnerName; //러너 이름
}
