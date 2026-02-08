package park.brothers.runwith_back.domain.Schedule.dto.Request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateScheduleRequestDto {

    @NotNull
    Long belongId;

    @NotNull
    int scheduleYear;

    @NotNull
    int scheduleMonth;

    @NotNull
    int scheduleDate;

    @NotEmpty
    String description;
}
