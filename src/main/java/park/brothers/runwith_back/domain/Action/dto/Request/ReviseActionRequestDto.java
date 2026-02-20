package park.brothers.runwith_back.domain.Action.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ReviseActionRequestDto {

    @NotEmpty
    String actionId;

    @NotEmpty
    String actionName;

    @NotEmpty
    String actionDescription;

    @NotNull
    int actionStartHour;

    @NotNull
    int actionStartMinute;

    @NotNull
    int actionEndHour;

    @NotNull
    int actionEndMinute;
}
