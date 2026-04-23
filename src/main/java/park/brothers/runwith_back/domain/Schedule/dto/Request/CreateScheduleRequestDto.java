package park.brothers.runwith_back.domain.Schedule.dto.Request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateScheduleRequestDto {

    @NotEmpty
    private String belongId;

    @NotNull
    private int scheduleYear;

    @NotNull
    private int scheduleMonth;

    @NotNull
    private int scheduleDate;

    @NotEmpty
    private String scheduleDescription;
}
