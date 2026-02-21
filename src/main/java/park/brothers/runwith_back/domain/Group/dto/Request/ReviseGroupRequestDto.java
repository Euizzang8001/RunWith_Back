package park.brothers.runwith_back.domain.Group.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class ReviseGroupRequestDto {

    @NotEmpty
    private String runnerId;

    private int groupCertificationCriteria;

    private String groupDescription;

    private String groupImageLink;
}
