package park.brothers.runwith_back.domain.JoinRequest.dto.response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class GetMyJoinRequestsResponseDto {
    @NotEmpty
    private String joinRequestId;

    @NotEmpty
    private String groupId;

    private LocalDateTime createdAt; //가입 신청한 시간
}
