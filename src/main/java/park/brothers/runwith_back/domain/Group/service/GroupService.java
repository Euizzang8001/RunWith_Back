package park.brothers.runwith_back.domain.Group.service;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.domain.Group.dto.Request.CreateGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Request.ReviseGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.CreateGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Response.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Group.dto.Request.DeleteGroupRequestDto;
import park.brothers.runwith_back.domain.Group.dto.Response.ReviseGroupResponseDto;

import java.io.IOException;
import java.util.List;

public interface GroupService {

    CreateGroupResponseDto save(CreateGroupRequestDto createGroupRequestDto, MultipartFile image) throws IllegalAccessError, IOException;

    List<GetGroupResponseDto> getAllGroups();

    List<GetGroupResponseDto> getGroupsBySimilarName(String name);

    void delete(DeleteGroupRequestDto deleteGroupRequestDto);

    ReviseGroupResponseDto reviseGroup(String groupId, @Valid ReviseGroupRequestDto reviseGroupRequestDto, MultipartFile image) throws IllegalAccessException, IOException;
}
