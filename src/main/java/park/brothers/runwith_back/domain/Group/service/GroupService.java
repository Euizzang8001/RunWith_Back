package park.brothers.runwith_back.domain.Group.service;

import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Request.DeleteGroupRequestDto;

import java.util.List;

public interface GroupService {

    void save(CreateGroupRequestDto createGroupRequestDto);

    List<GetGroupResponseDto> getAllGroups();

    GetGroupResponseDto getGroupByName(String name);

    List<GetGroupResponseDto> getGroupsBySimilarName(String name);

    void delete(DeleteGroupRequestDto deleteGroupRequestDto);
}
