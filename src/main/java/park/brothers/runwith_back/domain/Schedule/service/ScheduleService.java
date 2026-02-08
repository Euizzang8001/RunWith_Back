package park.brothers.runwith_back.domain.Schedule.service;

import jakarta.validation.Valid;
import park.brothers.runwith_back.domain.Schedule.dto.Request.CreateScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Request.ReviseScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.GetSchedulesResponseDto;

import java.time.LocalDate;
import java.util.List;

public interface ScheduleService {
    void create(@Valid CreateScheduleRequestDto createScheduleRequestDto);

    void delete(@Valid Long id);

    List<GetSchedulesResponseDto> getSchedules(@Valid Long belongId, @Valid LocalDate localDate);

    void revise(@Valid ReviseScheduleRequestDto reviseScheduleRequestDto);
}
