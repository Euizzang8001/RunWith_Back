package park.brothers.runwith_back.domain.Runner.dto.Response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class CreateRunnerResponseDto {

    @NotEmpty
    private String runnerName; //생성된 러너 이름

    private String runnerImageLink; //저장된 러너 이미지
}
