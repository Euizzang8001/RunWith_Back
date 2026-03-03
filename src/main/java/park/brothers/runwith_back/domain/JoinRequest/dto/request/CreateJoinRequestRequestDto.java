package park.brothers.runwith_back.domain.JoinRequest.dto.request;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateJoinRequestRequestDto {

    @NotEmpty
    private String groupId; //가입하고 싶은 그룹 id
}
