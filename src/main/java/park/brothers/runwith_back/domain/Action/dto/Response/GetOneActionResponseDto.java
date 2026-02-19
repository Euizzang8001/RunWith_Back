package park.brothers.runwith_back.domain.Action.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetOneActionResponseDto {
    @NotNull
    Long actionId;

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
