package park.brothers.runwith_back.domain.Group.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateGroupDto {
    @NotEmpty
    private String name;
}
