package park.brothers.runwith_back.domain.Group.service;

import park.brothers.runwith_back.domain.Group.dto.CreateGroupDto;
import park.brothers.runwith_back.domain.Group.dto.GetGroupResponseDto;

import java.util.List;

public interface GroupService {

    void save(CreateGroupDto createGroupDto);

    List<GetGroupResponseDto> getAllGroups();

    GetGroupResponseDto getGroupByName(String name);

    List<GetGroupResponseDto> getGroupsBySimilarName(String name);

    void delete(Long id);
}
