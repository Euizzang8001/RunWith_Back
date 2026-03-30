package park.brothers.runwith_back.domain.Schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.common.Exceptions.UnauthorizedException;
import park.brothers.runwith_back.domain.Action.entity.Action;
import park.brothers.runwith_back.domain.Action.repository.ActionRepository;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Recognize.entity.Recognize;
import park.brothers.runwith_back.domain.Recognize.repository.RecognizeRepository;
import park.brothers.runwith_back.domain.Schedule.dto.Request.CreateScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Request.ReviseScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.CreateScheduleResponseDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.GetMySchedulesResponseDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.GetSchedulesResponseDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.ReviseScheduleResponseDto;
import park.brothers.runwith_back.domain.Schedule.entity.Schedule;
import park.brothers.runwith_back.domain.Schedule.repository.ScheduleRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class Version1ScheduleService implements ScheduleService{
    private final ScheduleRepository scheduleRepository;
    private final BelongRepository belongRepository;
    private final ActionRepository actionRepository;
    private final RecognizeRepository recognizeRepository;

    //스케줄 생성
    @Override
    public CreateScheduleResponseDto create(String runnerId, CreateScheduleRequestDto createScheduleRequestDto) {
        //비어있는 belong인지 확인
        Optional<Belong> belong = belongRepository.findById(UUID.fromString(createScheduleRequestDto.getBelongId()));
        if(belong.isEmpty()){
            throw new ResourceNotFoundException("그룹에 속하지 않습니다.(belong이 없습니다.)");
        }

        //해당 belong이 러너의 것이 맞는지 확인
        if(!belong.get().getRunner().getId().equals(runnerId)){
            throw new UnauthorizedException("로그인한 러너의 belong이 아닙니다.");
        }

        //저장하기
        Schedule schedule = new Schedule();
        schedule.setBelong(belong.get());
        schedule.setDescription(createScheduleRequestDto.getScheduleDescription());
        schedule.setScheduleYear(createScheduleRequestDto.getScheduleYear());
        schedule.setScheduleMonth(createScheduleRequestDto.getScheduleMonth());
        schedule.setScheduleDate(createScheduleRequestDto.getScheduleDate());

        Schedule savedSchedule = scheduleRepository.save(schedule);
        return new CreateScheduleResponseDto(
                savedSchedule.getId().toString(),
                savedSchedule.getBelong().getId().toString(),
                savedSchedule.getScheduleYear(),
                savedSchedule.getScheduleMonth(),
                savedSchedule.getScheduleDate(),
                savedSchedule.getDescription()
        );
    }

    //스케줄 삭제
    @Override
    public void delete(String runnerId, String scheduleId) {
        Optional<Schedule> schedule = scheduleRepository.findScheduleById(UUID.fromString(scheduleId));

        //삭제하려는 스케줄이 없을 때
        if(schedule.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않는 스케줄입니다.");
        }

        //삭제하려는 스케줄이 로그인한 러너의 스케줄이 아닐 때
        if(!schedule.get().getBelong().getRunner().getId().equals(runnerId)){
            throw new UnauthorizedException("스케줄은 해당 러너만이 삭제할 수 있습니다.");
        }

        //해당 스케줄 id를 인정한 기록 삭제
        List<Recognize> recognizes = recognizeRepository.findByScheduleId(schedule.get().getId());
        for(Recognize recognize: recognizes){
            recognizeRepository.delete(recognize);
        }

        //해당 스케줄을 FK로 가진 Actions들 삭제
        List<Action> actions = actionRepository.findActionsByScheduleId(schedule.get().getId());
        for(Action action : actions){
            actionRepository.delete(action);
        }

        scheduleRepository.delete(schedule.get());
    }

    //특정 조건의 스케줄들 조회
    @Override
    public List<GetSchedulesResponseDto> getSchedules(String runnerId, String belongId, LocalDate localDate) {
        List<Schedule> schedules;
        if(belongId == null && localDate == null){
            schedules = scheduleRepository.findAllSchedule();
        } else if (belongId == null) {
            schedules = scheduleRepository.findByLocalDate(localDate);
        } else if (localDate == null) {
            schedules = scheduleRepository.findByBelongId(UUID.fromString(belongId));
        } else{
            schedules = scheduleRepository.findByBelongIdAndLocalDate(UUID.fromString(belongId), localDate);
        }

        return schedules.stream()
                .map(schedule -> new GetSchedulesResponseDto(
                        schedule.getId().toString(),
                        schedule.getBelong().getId().toString(),
                        schedule.getRecognizeCount(),
                        recognizeRepository.findByRunnerIdAndScheduleId(runnerId, schedule.getId())
                            .map(Recognize::isRecognizing)
                            .orElse(false),
                        schedule.getScheduleYear(),
                        schedule.getScheduleMonth(),
                        schedule.getScheduleDate(),
                        schedule.getDescription()))
                .collect(Collectors.toList());
    }

    //특정 스케줄 수정
    @Override
    public ReviseScheduleResponseDto revise(String runnerId, String scheduleId, ReviseScheduleRequestDto reviseScheduleRequestDto) {
        Optional<Schedule> schedule = scheduleRepository.findScheduleById(UUID.fromString(scheduleId));
        //비어있는 스케줄인지 확인
        if(schedule.isEmpty()){
            throw  new ResourceNotFoundException("존재하지 않는 스케줄입니다.");
        }
        //러너가 해당 스케줄의 주인인지 확인
        if(!schedule.get().getBelong().getRunner().getId().equals(runnerId)){
            throw new UnauthorizedException("스케줄은 해당 스케줄 소유 러너만 수정할 수 있습니다.");
        }

        scheduleRepository.reviseSchedule(schedule.get(), reviseScheduleRequestDto.getScheduleDescription());
        return new ReviseScheduleResponseDto(
                schedule.get().getId().toString(),
                schedule.get().getBelong().getId().toString(),
                schedule.get().getScheduleYear(),
                schedule.get().getScheduleMonth(),
                schedule.get().getScheduleDate(),
                reviseScheduleRequestDto.getScheduleDescription()
        );
    }

    @Override
    public List<GetMySchedulesResponseDto> getMySchedules(String runnerId, String groupId, LocalDate localDate) {
        //러너가 해당 그룹에 속하지 않으면 문제
        if(groupId != null){
            Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, UUID.fromString(groupId));
            if(belong.isEmpty()){
                throw new ResourceNotFoundException("해당 러너는 그룹에 속하지 않습니다.");
            }
        }
        List<Schedule> schedules;
        if(groupId == null && localDate == null){
            schedules = scheduleRepository.findScheduleByRunnerId(runnerId);
        } else if (groupId == null) {
            schedules = scheduleRepository.findByRunnerIdAndLocalDate(runnerId, localDate);
        } else{
            //러너가 해당 그룹에 속하지 않으면 문제
            Optional<Belong> belong = belongRepository.findByRunnerIdAndGroupId(runnerId, UUID.fromString(groupId));
            if(belong.isEmpty()){
                throw new ResourceNotFoundException("해당 러너는 그룹에 속하지 않습니다.");
            }
            if(localDate == null){
                schedules = scheduleRepository.findByBelongId(belong.get().getId());
            }
            else{
                schedules = scheduleRepository.findByBelongIdAndLocalDate(belong.get().getId(), localDate);
            }
        }

        return schedules.stream()
                .map(schedule -> new GetMySchedulesResponseDto(
                        schedule.getId().toString(),
                        schedule.getBelong().getId().toString(),
                        schedule.getRecognizeCount(),
                        schedule.getScheduleYear(),
                        schedule.getScheduleMonth(),
                        schedule.getScheduleDate(),
                        schedule.getDescription()))
                .collect(Collectors.toList());
    }
}
