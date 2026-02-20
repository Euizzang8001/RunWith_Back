package park.brothers.runwith_back.domain.Schedule.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import park.brothers.runwith_back.domain.Belong.entity.Belong;
import park.brothers.runwith_back.domain.Belong.repository.BelongRepository;
import park.brothers.runwith_back.domain.Schedule.dto.Request.CreateScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Request.ReviseScheduleRequestDto;
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
    private final BelongRepository belongRepository;

    //스케줄 생성
    @Override
    public void create(CreateScheduleRequestDto createScheduleRequestDto) {
        Schedule schedule = new Schedule();
        Optional<Belong> belong = belongRepository.findById(UUID.fromString(createScheduleRequestDto.getBelongId()));
        String description = createScheduleRequestDto.getScheduleDescription();
        if(belong.isEmpty()){
            throw new IllegalAccessError("그룹에 속하지 않습니다.");
        }

        schedule.setBelong(belong.get());
        schedule.setDescription(description);
        schedule.setScheduleYear(LocalDate.now().getYear());
        schedule.setScheduleMonth(LocalDate.now().getMonthValue());
        schedule.setScheduleDate(LocalDate.now().getDayOfMonth());

        scheduleRepository.save(schedule);
    }

    //스케줄 삭제
    @Override
    public void delete(String id) {
        scheduleRepository.delete(UUID.fromString(id));
    }

    //특정 조건의 스케줄들 조회
    @Override
    public List<GetSchedulesResponseDto> getSchedules(String belongId, LocalDate localDate) {
        List<Schedule> schedules;
        if(belongId == null && localDate == null){
            schedules = scheduleRepository.fintAllSchedule();
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
    public void revise(ReviseScheduleRequestDto reviseScheduleRequestDto) {
        String id = reviseScheduleRequestDto.getScheduleId();
        String description = reviseScheduleRequestDto.getScheduleDescription();
        scheduleRepository.reviseSchedule(UUID.fromString(id), description);
    }
}
