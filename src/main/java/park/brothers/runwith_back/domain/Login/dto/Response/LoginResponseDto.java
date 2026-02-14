package park.brothers.runwith_back.domain.Login.dto.Response;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDto {

    @NotNull
    private Long id;
}
