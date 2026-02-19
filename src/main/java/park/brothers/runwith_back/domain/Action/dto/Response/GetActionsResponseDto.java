package park.brothers.runwith_back.domain.Action.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetActionsResponseDto {
    @NotNull
    Long actionId;

    @NotEmpty
    String name;

    @NotNull
    int startHour;

    @NotNull
    int startMinute;

    @NotNull
    int endHour;

    @NotNull
    int endMinute;
}
