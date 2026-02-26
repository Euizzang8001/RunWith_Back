package park.brothers.runwith_back.domain.Belong.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeLeaderResponseDto {

    @NotEmpty
    private String newLeaderRunnerId;

    @NotEmpty
    private String groupId;
}
