package park.brothers.runwith_back.domain.Schedule.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetMySchedulesResponseDto {

    @NotEmpty
    private String scheduleId;

    @NotEmpty
    private String belongId;

    @NotNull
    private int recognizeCount; //인정 수

    @NotNull
    private int scheduleYear;

    @NotNull
    private int scheduleMonth;

    @NotNull
    private int scheduleDate;

    @NotEmpty
    private String scheduleDescription;
}
