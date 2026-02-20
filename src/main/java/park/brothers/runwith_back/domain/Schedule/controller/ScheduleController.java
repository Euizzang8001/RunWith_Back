package park.brothers.runwith_back.domain.Schedule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Schedule.dto.Request.CreateScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Request.ReviseScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.GetSchedulesResponseDto;
import park.brothers.runwith_back.domain.Schedule.service.ScheduleService;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/schedules")
public class ScheduleController {
    private final ScheduleService scheduleService;

    //스케줄 생성
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody @Valid CreateScheduleRequestDto createScheduleRequestDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }
        scheduleService.create(createScheduleRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(createScheduleRequestDto);

    }

    //스케줄 삭제
    @DeleteMapping
    public ResponseEntity<Object> delete(@RequestParam @Valid String id){
        scheduleService.delete(id);
        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    //스케줄 조회
    @GetMapping
    public ResponseEntity<Object> getSchedules(
            @RequestParam(required = false) @Valid String belongId,
            @RequestParam(required = false) @Valid @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate
    ) {
      List<GetSchedulesResponseDto> schedules = scheduleService.getSchedules(belongId, localDate);
      return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    //스케줄 수정
    @PatchMapping
    public ResponseEntity<Object> revise(
            @RequestBody @Valid ReviseScheduleRequestDto reviseScheduleRequestDto,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }
        scheduleService.revise(reviseScheduleRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(reviseScheduleRequestDto);
    }

}
