package park.brothers.runwith_back.domain.Group.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class GetGroupResponseDto {

    @NotEmpty
    private String groupId;

    @NotEmpty
    private String groupName;

    @NotEmpty
    private String groupDescription;

    private String groupImageLink;
}
