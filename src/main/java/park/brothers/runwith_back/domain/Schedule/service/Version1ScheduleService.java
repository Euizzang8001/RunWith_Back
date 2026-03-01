package park.brothers.runwith_back.domain.Schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.common.Exceptions.UnauthorizedException;
import park.brothers.runwith_back.domain.Action.repository.ActionRepository;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Schedule.dto.Request.CreateScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Request.ReviseScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.CreateScheduleResponseDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.GetSchedulesResponseDto;
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
    private final ActionRepository actionRepository;
    private final BelongRepository belongRepository;

    //스케줄 생성
    @Override
    public CreateScheduleResponseDto create(String runnerId, CreateScheduleRequestDto createScheduleRequestDto) {
        //그룹에 속하지 않으면 에러
        String belongStrId = createScheduleRequestDto.getBelongId();
        Optional<Belong> belong = belongRepository.findById(UUID.fromString(belongStrId));
        if(belong.isEmpty()){
            throw new ResourceNotFoundException("그룹에 속하지 않습니다.");
        }

        //해당 소속 정보와 러너의 정보가 다르면 오류
        if(!belong.get().getRunner().getId().equals(runnerId)){
            throw new UnauthorizedException("자신의 스케줄만 수정할 수 있습니다.");
        }

        //위 오류를 통과하면 스케줄 저장
        Schedule schedule = new Schedule();
        schedule.setBelong(belong.get());
        schedule.setDescription(createScheduleRequestDto.getScheduleDescription());
        schedule.setScheduleYear(createScheduleRequestDto.getScheduleYear());
        schedule.setScheduleMonth(createScheduleRequestDto.getScheduleMonth());
        schedule.setScheduleDate(createScheduleRequestDto.getScheduleDate());

        Schedule savedSchedule = scheduleRepository.save(schedule);

        return new CreateScheduleResponseDto(
                savedSchedule.getId().toString(),
                belongStrId,
                createScheduleRequestDto.getScheduleYear(),
                createScheduleRequestDto.getScheduleMonth(),
                createScheduleRequestDto.getScheduleDate(),
                createScheduleRequestDto.getScheduleDescription()
        );
    }

    //스케줄 삭제
    @Override
    public void delete(String runnerId, String scheduleId) {
        Optional<Schedule> schedule = scheduleRepository.findScheduleById(UUID.fromString(scheduleId));
        //삭제하려는 스케줄이 존재하지 않음
        if(schedule.isEmpty()){
            throw new ResourceNotFoundException("존재하지 않은 스케줄입니다.");
        }
        //삭제하려는 스케줄의 주인이 요청한 러너와 다르면 에러
        if(!schedule.get().getBelong().getRunner().getId().equals(runnerId)){
            throw new UnauthorizedException("자신의 스케줄만 삭제할 수 있습니다.");
        }

        scheduleRepository.delete(schedule.get());
        //action의 repository에 deleteByScheduleId 구현되면 함께 수정
    }

    //특정 조건의 스케줄들 조회(오름차순)
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
                        schedule.getScheduleYear(),
                        schedule.getScheduleMonth(),
                        schedule.getScheduleDate(),
                        schedule.getDescription()))
                .collect(Collectors.toList());
    }

    //특정 스케줄 수정
    @Override
    public void revise(String runnerId, String scheduleId, ReviseScheduleRequestDto reviseScheduleRequestDto) {
        String id = reviseScheduleRequestDto.getScheduleId();
        String description = reviseScheduleRequestDto.getScheduleDescription();
        scheduleRepository.reviseSchedule(UUID.fromString(id), description);
    }
}
