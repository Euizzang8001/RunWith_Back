package park.brothers.runwith_back.domain.Belong.service;

import jakarta.validation.Valid;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.JoinGroupRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.LeaveGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetRunnerResponseDto;

import java.util.List;

public interface BelongService {
    void joinGroup(@Valid JoinGroupRequestDto joinGroupRequestDto);

    void leaveGroup(@Valid LeaveGroupRequestDto leaveGroupRequestDto);

    List<GetGroupResponseDto> getAllGroupsRunnerJoin(@Valid Long runnerId);

    List<GetRunnerResponseDto> getAllRunnersInGroup(@Valid Long groupId);

    void changeLeader(@Valid ChangeLeaderRequestDto changeLeaderRequestDto);
}
