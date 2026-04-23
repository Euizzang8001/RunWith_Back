package park.brothers.runwith_back.domain.Action.service;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.common.Exceptions.NotAcceptableException;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.common.Exceptions.UnauthorizedException;
import park.brothers.runwith_back.domain.Action.dto.Request.CreateActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Request.ReviseActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Response.CreateActionResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetActionsResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetOneActionResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.ReviseActionResponseDto;
import park.brothers.runwith_back.domain.Action.entity.Action;
import park.brothers.runwith_back.domain.Action.repository.ActionRepository;
import park.brothers.runwith_back.domain.Schedule.entity.Schedule;
import park.brothers.runwith_back.domain.Schedule.repository.ScheduleRepository;
import park.brothers.runwith_back.external.AWS_S3.AWSS3Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Primary
public class Version1ActionService implements ActionService{

    private final ActionRepository actionRepository;
    private final ScheduleRepository scheduleRepository;
    private final AWSS3Service awss3Service;

    //action 생성하기
    @Override
    @Transactional
    public CreateActionResponseDto createAction(String runnerId, CreateActionRequestDto createActionRequestDto, List<MultipartFile> images) throws IOException {
        //유효한 스케줄이어야 한다.
        String scheduleId = createActionRequestDto.getScheduleId();
        Optional<Schedule> schedule = scheduleRepository.findScheduleById(UUID.fromString(scheduleId));
        if(schedule.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 스케줄입니다.");
        }
        //일정은 겹쳐서는 안된다.
        int startHour = createActionRequestDto.getActionStartHour();
        int startMinute = createActionRequestDto.getActionStartMinute();
        int endHour = createActionRequestDto.getActionEndHour();
        int endMinute = createActionRequestDto.getActionEndMinute();
        List<Action> overlappedActions = actionRepository.findOverlappedActions(UUID.fromString(scheduleId), startHour, startMinute, endHour, endMinute);
        if (!overlappedActions.isEmpty()) {
            throw new NotAcceptableException("이미 일정이 존재하는 시간대입니다.");
        }

        Action action = new Action();
        action.setDescription(createActionRequestDto.getActionDescription());
        action.setName(createActionRequestDto.getActionName());
        action.setStartHour(startHour);
        action.setStartMinute(startMinute);
        action.setEndHour(endHour);
        action.setEndMinute(endMinute);
        action.setSchedule(schedule.get());
        action.setMaxImageSize(images.size());
        Action savedAction = actionRepository.save(action);

        //이미지가 있다면 이미지 저장
        List<String> imageLinkList = new ArrayList<>();
        if (!images.isEmpty()) {
            int i = 0;
            for (MultipartFile image : images) {
                if (image != null && !image.isEmpty()) {
                    String imageLink = awss3Service.putImageToAWSS3(
                            image,
                            "actions",
                            savedAction.getId().toString(),
                            i++
                    );
                    imageLinkList.add(imageLink);
                }
            }
        }

        return new CreateActionResponseDto(
                savedAction.getId().toString(),
                schedule.get().getId().toString(),
                savedAction.getName(),
                savedAction.getDescription(),
                savedAction.getStartHour(),
                savedAction.getStartMinute(),
                savedAction.getEndHour(),
                savedAction.getEndMinute(),
                imageLinkList
        );
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
    @Transactional
    public void deleteAction(String runnerId, String actionId) {
        Optional<Action> action = actionRepository.findById(UUID.fromString(actionId));
        //액션이 존재하지 않으면 에러
        if(action.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 액션입니다.");
        }

        //해당 액션의 주인인 러너가 삭제하려면 오류
        if(!action.get().getSchedule().getBelong().getRunner().getId().equals(runnerId)) {
            throw new UnauthorizedException("해당 액션을 소유한 러너만이 삭제할 수 있습니다.");
        }
        //이미지 삭제
        for(int i = 0; i < action.get().getMaxImageSize() ; i++){
            awss3Service.deleteImageFromS3("actions", actionId, i);
        }

        // 액션 삭제
        actionRepository.delete(action.get());
    }

    //Action 수정
    @Override
    @Transactional
    public ReviseActionResponseDto reviseAction(String runnerId, String actionId, ReviseActionRequestDto reviseActionRequestDto, List<MultipartFile> images) throws IOException {
        Optional<Action> action = actionRepository.findById(UUID.fromString(actionId));
        //액션이 존재하지 않음
        if(action.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 액션입니다.");
        }

        //러너가 이 액션의 주인이 아님
        if(!action.get().getSchedule().getBelong().getRunner().getId().equals(runnerId)){
            throw new UnauthorizedException("해당 액션을 소유한 러너만이 수정할 수 있습니다.");
        }

        //겹치는 시간대 보기
        int startHour = reviseActionRequestDto.getActionStartHour();
        int startMinute = reviseActionRequestDto.getActionStartMinute();
        int endHour = reviseActionRequestDto.getActionEndHour();
        int endMinute = reviseActionRequestDto.getActionEndMinute();
        List<Action> overlappedActions = actionRepository.findOverlappedActions(UUID.fromString(actionId), startHour, startMinute, endHour, endMinute);
        if ((overlappedActions.size() == 1 && !overlappedActions.getFirst().getId().equals(UUID.fromString(actionId))) || (overlappedActions.size() >= 2)) {
            throw new NotAcceptableException("이미 일정이 존재하는 시간대입니다.");
        }

        String name = reviseActionRequestDto.getActionName();
        String description = reviseActionRequestDto.getActionDescription();


        //이미지 수정
        for(int i = 0; i < action.get().getMaxImageSize(); i++){
            awss3Service.deleteImageFromS3("actions", actionId, i);
        }
        List<String> imageLinkList = new ArrayList<>(List.of());
        for(int i = 0; i < images.size(); i++){
            imageLinkList.add(awss3Service.putImageToAWSS3(images.get(i), "actions", actionId, i));
        }

        //액션 수정
        Action revisedAction = actionRepository.reviseAction(action.get(), name, description, startHour, startMinute, endHour, endMinute, imageLinkList.size());

        return new ReviseActionResponseDto(
            revisedAction.getId().toString(),
                revisedAction.getSchedule().getId().toString(),
                revisedAction.getName(),
                revisedAction.getDescription(),
                revisedAction.getStartHour(),
                revisedAction.getStartMinute(),
                revisedAction.getEndHour(),
                revisedAction.getEndMinute(),
                imageLinkList
        );
    }


    //하나의 action 상세 보기
    @Override
    public GetOneActionResponseDto getActionById(String id) {
        Optional<Action> action = actionRepository.findById(UUID.fromString(id));
        if(action.isEmpty()){
            throw new IllegalAccessError("존재하지 않는 액션입니다.");
        }
        List<String> imageLinkList = new ArrayList<>(List.of());

        for(int i = 0; i < action.get().getMaxImageSize(); i++){
            imageLinkList.add(awss3Service.getImagePresignedUrl("actions", action.get().getId().toString(), i));
        }

        return new GetOneActionResponseDto(
                action.get().getId().toString(),
                action.get().getName(),
                action.get().getDescription(),
                action.get().getStartHour(),
                action.get().getStartMinute(),
                action.get().getEndHour(),
                action.get().getEndMinute(),
                imageLinkList
        );
    }
}
