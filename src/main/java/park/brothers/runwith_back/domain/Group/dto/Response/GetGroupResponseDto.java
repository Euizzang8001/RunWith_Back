package park.brothers.runwith_back.domain.Group.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class GetGroupResponseDto {

    @NotNull
    private Long groupId;

    @NotEmpty
    private String name;

    @NotEmpty
    private String description;

    private String imageLink;
}
