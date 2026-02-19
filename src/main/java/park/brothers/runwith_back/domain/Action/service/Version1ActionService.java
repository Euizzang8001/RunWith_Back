package park.brothers.runwith_back.domain.Action.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Action.dto.Request.CreateActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Request.ReviseActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetActionsResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetOneActionResponseDto;
import park.brothers.runwith_back.domain.Action.entity.Action;
import park.brothers.runwith_back.domain.Action.repository.ActionRepository;
import park.brothers.runwith_back.domain.Schedule.entity.Schedule;
import park.brothers.runwith_back.domain.Schedule.repository.ScheduleRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@Primary
public class Version1ActionService implements ActionService{

    private final ActionRepository actionRepository;
    private final ScheduleRepository scheduleRepository;

    //action 생성
    @Override
    public void createAction(CreateActionRequestDto createActionRequestDto) {
        Long scheduleId = createActionRequestDto.getScheduleId();
        int startHour = createActionRequestDto.getActionStartHour();
        int startMinute = createActionRequestDto.getActionStartMinute();
        int endHour = createActionRequestDto.getActionEndHour();
        int endMinute = createActionRequestDto.getActionEndMinute();

        List<Action> overlappedActions = actionRepository.getOverlappedActions(scheduleId, startHour, startMinute, endHour, endMinute);
        if (!overlappedActions.isEmpty()) {
            throw new IllegalArgumentException("이미 일정이 존재하는 시간대입니다.");
        }

        Action action = new Action();
        action.setDescription(createActionRequestDto.getActionDescription());
        action.setName(createActionRequestDto.getActionName());
        action.setStartHour(startHour);
        action.setStartMinute(startMinute);
        action.setEndHour(endHour);
        action.setEndMinute(endMinute);

        Schedule schedule = scheduleRepository.getScheduleById(scheduleId);
        action.setSchedule(schedule);

        actionRepository.save(action);
    }

    //Actions 조회하기
    @Override
    public List<GetActionsResponseDto> getActionsByScheduleId(Long scheduleId) {
        List<Action> actions;
        actions = actionRepository.getActionsByScheduleId(scheduleId);

        return actions.stream()
                .map(action -> new GetActionsResponseDto(
                        action.getId(),
                        action.getName(),
                        action.getStartHour(),
                        action.getStartMinute(),
                        action.getEndHour(),
                        action.getEndMinute()
                ))
                .collect(Collectors.toList());
    }

    //Action 삭제
    @Override
    public void deleteAction(Long id) {
        Action action = actionRepository.getById(id);
        actionRepository.delete(action);
    }

    //Action 수정
    @Override
    public void reviseAction(ReviseActionRequestDto reviseActionRequestDto) {
        String name = reviseActionRequestDto.getActionName();
        String description = reviseActionRequestDto.getActionDescription();
        int startHour = reviseActionRequestDto.getActionStartHour();
        int startMinute = reviseActionRequestDto.getActionStartMinute();
        int endHour = reviseActionRequestDto.getActionEndHour();
        int endMinute = reviseActionRequestDto.getActionEndMinute();
        Long id = reviseActionRequestDto.getActionId();

        actionRepository.reviseAction(id, name, description, startHour, startMinute, endHour, endMinute);
    }

    @Override
    public GetOneActionResponseDto getActionById(Long id) {
        Action action = actionRepository.getById(id);
        return new GetOneActionResponseDto(
                action.getId(),
                action.getName(),
                action.getDescription(),
                action.getStartHour(),
                action.getStartMinute(),
                action.getEndHour(),
                action.getEndMinute()
        );
    }
}
