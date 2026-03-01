package park.brothers.runwith_back.domain.Schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
    @Operation(summary = "스케줄 생성",description = "특정 러너가 특정 그룹에서의 일일 스케줄을 생성합니다.")
    public ResponseEntity<Object> create(
            @AuthenticationPrincipal @Valid String runnerId,
            @Parameter(
                    description = "스케줄 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
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
    @Operation(summary = "스케줄 삭제",description = "스케줄을 삭제합니다.")
    public ResponseEntity<Object> delete(
            @AuthenticationPrincipal @Valid String runnerId,
            @Parameter(
                    description = "삭제할 스케줄 ID"
            )
            @PathVariable @Valid String scheduleId){
        scheduleService.delete(runnerId, scheduleId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("스케줄이 성공적으로 삭제되었습니다."));
    }

    //스케줄 조회
    @GetMapping
    @Operation(summary = "스케줄 조회",description = "오늘을 입력하면, 해당 달의 스케줄들을 조회합니다.")
    public ResponseEntity<Object> getSchedules(
            @AuthenticationPrincipal @Valid String runnerId,
            @Parameter(
                    description = "검색할 Belong Id"
            )
            @RequestParam(required = false) @Valid String belongId,
            @Parameter(
                    description = "오늘의 날짜 ex) 2026-03-01"
            )
            @RequestParam(required = false) @Valid @DateTimeFormat(pattern = "yyyy-MM") LocalDate localDate
    ) {
      List<GetSchedulesResponseDto> schedules = scheduleService.getSchedules(runnerId, belongId, localDate);
      return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    //스케줄 수정
    @PatchMapping("/{scheduleId}")
    @Operation(summary = "스케줄 수정",description = "스케줄 정보를 수정합니다.")
    public ResponseEntity<Object> revise(
            @AuthenticationPrincipal @Valid String runnerId,
            @Parameter(
                    description = "수정할 스케줄 ID"
            )
            @PathVariable @Valid String scheduleId,
            @Parameter(
                    description = "스케줄 수정 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
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
