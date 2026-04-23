package park.brothers.runwith_back.domain.Action.dto.Request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateActionRequestDto {

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
}
