package park.brothers.runwith_back.domain.JoinRequest.dto.response;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;

@Data
@Setter
@AllArgsConstructor
public class CreateJoinRequestResponseDto {

    @NotEmpty
    private String joinRequestId; //가입 신청 uuid

    @NotEmpty
    private String groupId; //가입할 그룹 id

    @NotEmpty
    private String groupName; //가입할 그룹 이름

    private LocalDateTime createdAt; //가입 신청한 시간
}
