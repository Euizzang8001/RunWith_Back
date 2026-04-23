package park.brothers.runwith_back.domain.Schedule.service;

import jakarta.validation.Valid;
import park.brothers.runwith_back.domain.Schedule.dto.Request.CreateScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Request.ReviseScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.CreateScheduleResponseDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.GetMySchedulesResponseDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.GetSchedulesResponseDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.ReviseScheduleResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    CreateScheduleResponseDto create(String runnerId, @Valid CreateScheduleRequestDto createScheduleRequestDto);

    void delete(String runnerId, String scheduleId);

    List<GetSchedulesResponseDto> getSchedules(String runnerId, @Valid String belongId, @Valid LocalDate localDate);

    ReviseScheduleResponseDto revise(String runnerId, String scheduleId, @Valid ReviseScheduleRequestDto reviseScheduleRequestDto);

    List<GetMySchedulesResponseDto> getMySchedules(String runnerId, String groupId, LocalDate localDate);
}
