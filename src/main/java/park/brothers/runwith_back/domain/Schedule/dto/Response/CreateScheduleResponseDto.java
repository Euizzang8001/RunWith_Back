package park.brothers.runwith_back.domain.Schedule.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateScheduleResponseDto {

    @NotEmpty
    private String scheduleId; //생성된 스케줄 id

    @NotEmpty
    private String belongId; //스케줄을 소유한 belong객체

    @NotNull
    private int scheduleYear; //스케줄 연도

    @NotNull
    private int scheduleMonth; //스케줄 월

    @NotNull
    private int scheduleDate; //스케줄 일

    @NotEmpty
    private String scheduleDescription; //스케줄 정보
}
