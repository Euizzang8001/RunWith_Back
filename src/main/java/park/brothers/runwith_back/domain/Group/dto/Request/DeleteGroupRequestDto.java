package park.brothers.runwith_back.domain.Group.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteGroupRequestDto {

    @NotEmpty
    private Long groupId;

    @NotEmpty
    private Long runnerId;
}
