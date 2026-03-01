package park.brothers.runwith_back.domain.Belong.service;

import jakarta.validation.Valid;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.CreateBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.DeleteBelongRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.ChangeLeaderResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.CreateBelongResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.GetBelongOfGroupResponseDto;
import park.brothers.runwith_back.domain.Belong.dto.Response.GetBelongOfRunnerResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetRunnerResponseDto;

import java.util.List;

public interface BelongService {
    CreateBelongResponseDto joinGroup(String runnerId, @Valid CreateBelongRequestDto createBelongRequestDto);

    void leaveGroup(String runnerId, String groupId);

    List<GetBelongOfGroupResponseDto> getAllGroupsRunnerJoin(@Valid String runnerId);

    List<GetBelongOfRunnerResponseDto> getAllRunnersInGroup(@Valid String groupId);

    void changeLeader(String oldLeaderRunnerId, @Valid ChangeLeaderRequestDto changeLeaderRequestDto);
}
