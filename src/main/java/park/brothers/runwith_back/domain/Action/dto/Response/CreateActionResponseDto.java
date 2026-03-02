package park.brothers.runwith_back.domain.Action.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CreateActionResponseDto {

    @NotEmpty
    private String actionId;

    @NotEmpty
    private String scheduleId;

    @NotEmpty
    private String actionName;

    @NotEmpty
    private String actionDescription;

    @NotNull
    private int actionStartHour;

    @NotNull
    private int actionStartMinute;

    @NotNull
    private int actionEndHour;

    @NotNull
    private int actionEndMinute;

    private List<String> actionImageLinks;

}
