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
import java.util.Optional;
import java.util.UUID;
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
        String scheduleId = createActionRequestDto.getScheduleId();
        int startHour = createActionRequestDto.getActionStartHour();
        int startMinute = createActionRequestDto.getActionStartMinute();
        int endHour = createActionRequestDto.getActionEndHour();
        int endMinute = createActionRequestDto.getActionEndMinute();

        List<Action> overlappedActions = actionRepository.findOverlappedActions(UUID.fromString(scheduleId), startHour, startMinute, endHour, endMinute);
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

        Optional<Schedule> schedule = scheduleRepository.findScheduleById(UUID.fromString(scheduleId));
        if(schedule.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 스케줄입니다.");
        }
        action.setSchedule(schedule.get());

        actionRepository.save(action);
    }

    //Actions 조회하기
    @Override
    public List<GetActionsResponseDto> getActionsByScheduleId(String scheduleId) {
        List<Action> actions;
        actions = actionRepository.findActionsByScheduleId(UUID.fromString(scheduleId));

        return actions.stream()
                .map(action -> new GetActionsResponseDto(
                        action.getId().toString(),
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
    public void deleteAction(String id) {
        Optional<Action> action = actionRepository.findById(UUID.fromString(id));
        action.ifPresent(actionRepository::delete);
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
        String id = reviseActionRequestDto.getActionId();

        actionRepository.reviseAction(UUID.fromString(id), name, description, startHour, startMinute, endHour, endMinute);
    }

    @Override
    public GetOneActionResponseDto getActionById(String id) {
        Optional<Action> action = actionRepository.findById(UUID.fromString(id));
        if(action.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 액션입니다.");
        }
        return new GetOneActionResponseDto(
                action.get().getId().toString(),
                action.get().getName(),
                action.get().getDescription(),
                action.get().getStartHour(),
                action.get().getStartMinute(),
                action.get().getEndHour(),
                action.get().getEndMinute()
        );
    }
}
