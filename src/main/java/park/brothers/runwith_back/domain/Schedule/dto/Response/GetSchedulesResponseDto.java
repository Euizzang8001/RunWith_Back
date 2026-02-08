package park.brothers.runwith_back.domain.Schedule.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetSchedulesResponseDto {

    @NotNull
    Long id;

    @NotNull
    int scheduleYear;

    @NotNull
    int scheduleMonth;

    @NotNull
    int scheduleDate;

    @NotEmpty
    String description;
}
