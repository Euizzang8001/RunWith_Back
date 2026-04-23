package park.brothers.runwith_back.domain.Recognize.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChangeRecognizeResponseDto {

    @NotEmpty
    private String scheduleId;

    private boolean recognizing; //인정 여부
}
