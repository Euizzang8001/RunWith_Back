package park.brothers.runwith_back.domain.Action.service;

import jakarta.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.domain.Action.dto.Request.CreateActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Request.ReviseActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Response.CreateActionResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetActionsResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetOneActionResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.ReviseActionResponseDto;

import java.io.IOException;
import java.util.List;

public interface ActionService {
    CreateActionResponseDto createAction(String runnerId, @Valid CreateActionRequestDto createActionRequestDto, List<MultipartFile> images) throws IOException;

    List<GetActionsResponseDto> getActionsByScheduleId(@Valid String scheduleId);

    void deleteAction(String runnerId, @Valid String id);

    ReviseActionResponseDto reviseAction(String runnerId, String actionId, @Valid ReviseActionRequestDto reviseActionRequestDto, List<MultipartFile> images) throws IOException;

    GetOneActionResponseDto getActionById(@Valid String id);
}
