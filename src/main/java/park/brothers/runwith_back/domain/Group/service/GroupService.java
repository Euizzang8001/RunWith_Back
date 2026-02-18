package park.brothers.runwith_back.domain.Group.service;

import jakarta.validation.Valid;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Request.ReviseGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.CreateGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Request.DeleteGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.ReviseGroupResponseDto;

import java.util.List;

public interface GroupService {

    CreateGroupResponseDto save(CreateGroupRequestDto createGroupRequestDto) throws IllegalAccessError;

    List<GetGroupResponseDto> getAllGroups();

    List<GetGroupResponseDto> getGroupsBySimilarName(String name);

    void delete(DeleteGroupRequestDto deleteGroupRequestDto);

    ReviseGroupResponseDto reviseGroup(@Valid ReviseGroupRequestDto reviseGroupRequestDto) throws IllegalAccessException;
}
