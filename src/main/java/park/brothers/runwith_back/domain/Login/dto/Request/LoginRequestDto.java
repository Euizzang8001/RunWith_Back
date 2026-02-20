package park.brothers.runwith_back.domain.Login.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequestDto {

    @NotEmpty
    private String loginEmail;

    @NotEmpty
    private String loginPassword;
}
