package park.brothers.runwith_back.domain.Action.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetActionsResponseDto {
    @NotEmpty
    String actionId;

    @NotEmpty
    String actionName;

    @NotNull
    int actionStartHour;

    @NotNull
    int actionStartMinute;

    @NotNull
    int actionEndHour;

    @NotNull
    int actionEndMinute;
}
