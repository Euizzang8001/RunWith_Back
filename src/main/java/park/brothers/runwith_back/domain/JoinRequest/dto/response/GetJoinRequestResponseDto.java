package park.brothers.runwith_back.domain.JoinRequest.dto.response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Setter
@AllArgsConstructor
public class GetJoinRequestResponseDto {

    @NotEmpty
    private String joinRequestId;

    @NotEmpty
    private String runnerId;

    private LocalDateTime createdAt; //가입 신청한 시간
}
