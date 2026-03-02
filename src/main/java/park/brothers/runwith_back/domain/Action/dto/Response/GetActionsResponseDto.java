package park.brothers.runwith_back.domain.Action.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetActionsResponseDto {
    @NotEmpty
    private String actionId;

    @NotEmpty
    private String actionName;

    @NotNull
    private int actionStartHour;

    @NotNull
    private int actionStartMinute;

    @NotNull
    private int actionEndHour;

    @NotNull
    private int actionEndMinute;
}
