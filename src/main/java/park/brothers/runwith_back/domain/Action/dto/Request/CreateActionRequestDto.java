package park.brothers.runwith_back.domain.Action.dto.Request;


import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateActionRequestDto {

    @NotNull
    Long scheduleId;

    @NotEmpty
    String name;

    @NotEmpty
    String description;

    @NotNull
    int startHour;

    @NotNull
    int startMinute;

    @NotNull
    int endHour;

    @NotNull
    int endMinute;
}
