package park.brothers.runwith_back.domain.Group.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteGroupRequestDto {

    @NotNull
    private Long groupId;

    @NotNull
    private Long runnerId;
}
