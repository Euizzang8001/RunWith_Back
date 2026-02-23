package park.brothers.runwith_back.domain.Belong.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteBelongRequestDto {

    @NotEmpty
    private String runnerId;

    @NotEmpty
    private String groupId;
}

