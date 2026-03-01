package park.brothers.runwith_back.domain.Schedule.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Schedule.dto.Request.CreateScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Request.ReviseScheduleRequestDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.CreateScheduleResponseDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.GetSchedulesResponseDto;
import park.brothers.runwith_back.domain.Schedule.dto.Response.ReviseScheduleResponseDto;
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
    public ResponseEntity<Object> create(
            @AuthenticationPrincipal @Valid String runnerId,
            @RequestBody @Valid CreateScheduleRequestDto createScheduleRequestDto,
            BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }
        CreateScheduleResponseDto createScheduleResponseDto = scheduleService.create(runnerId, createScheduleRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createScheduleResponseDto);
    }

    //스케줄 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<Object> delete(
            @AuthenticationPrincipal @Valid String runnerId,
            @PathVariable @Valid String scheduleId){
        scheduleService.delete(runnerId, scheduleId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("스케줄이 성공적으로 삭제되었습니다."));
    }

    //스케줄 조회
    @GetMapping
    public ResponseEntity<Object> getSchedules(
            @AuthenticationPrincipal @Valid String runnerId,
            @RequestParam(required = false) @Valid String belongId,
            @RequestParam(required = false) @Valid @DateTimeFormat(pattern = "yyyy-MM") LocalDate localDate
    ) {
      List<GetSchedulesResponseDto> schedules = scheduleService.getSchedules(runnerId, belongId, localDate);
      return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    //스케줄 수정
    @PatchMapping("/{scheduleId}")
    public ResponseEntity<Object> revise(
            @AuthenticationPrincipal @Valid String runnerId,
            @PathVariable @Valid String scheduleId,
            @RequestBody @Valid ReviseScheduleRequestDto reviseScheduleRequestDto,
            BindingResult bindingResult
    ){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }
        ReviseScheduleResponseDto reviseScheduleResponseDto = scheduleService.revise(runnerId, scheduleId, reviseScheduleRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(reviseScheduleResponseDto);
    }

}
