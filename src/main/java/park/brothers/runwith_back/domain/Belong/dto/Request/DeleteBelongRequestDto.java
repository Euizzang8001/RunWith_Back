package park.brothers.runwith_back.domain.Belong.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class DeleteBelongRequestDto {

    @NotEmpty
    private String runnerId;

    @NotEmpty
    private String groupId;
}

