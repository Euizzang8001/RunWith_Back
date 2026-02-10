package park.brothers.runwith_back.domain.Action.service;

import jakarta.validation.Valid;
import park.brothers.runwith_back.domain.Action.dto.Request.CreateActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Request.ReviseActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetActionsResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetOneActionResponseDto;

import java.util.List;

public interface ActionService {
    void createAction(@Valid CreateActionRequestDto createActionRequestDto);

    List<GetActionsResponseDto> getActionsByScheduleId(@Valid Long scheduleId);

    void deleteAction(@Valid Long id);

    void reviseAction(@Valid ReviseActionRequestDto reviseActionRequestDto);

    GetOneActionResponseDto getActionById(@Valid Long id);
}
