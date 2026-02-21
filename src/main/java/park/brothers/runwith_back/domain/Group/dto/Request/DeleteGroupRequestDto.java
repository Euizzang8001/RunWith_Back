package park.brothers.runwith_back.domain.Group.dto.Request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeleteGroupRequestDto {

    @NotEmpty
    private String runnerId; //삭제하려는 러너 id : 리더 아니면 안됨
}
