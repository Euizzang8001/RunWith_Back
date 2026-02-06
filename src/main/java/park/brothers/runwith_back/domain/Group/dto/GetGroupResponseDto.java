package park.brothers.runwith_back.domain.Group.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class GetGroupResponseDto {

    @NotEmpty
    private Long id;

    @NotEmpty
    private String name;
}
