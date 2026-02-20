package park.brothers.runwith_back.domain.Group.dto.Response;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@AllArgsConstructor
@Getter
public class ReviseGroupResponseDto {
    @NotEmpty
    private String groupId;

    @NotEmpty
    private String groupName;

    @NotEmpty
    private String groupDescription;

    private String groupImageLink;

    @NotNull
    private int groupCertificationCriteria;
}
