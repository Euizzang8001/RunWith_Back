package park.brothers.runwith_back.domain.Schedule.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.servlet.http.HttpServletRequest;
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
import park.brothers.runwith_back.domain.Schedule.dto.Response.GetMySchedulesResponseDto;
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
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "스케줄 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestBody @Valid CreateScheduleRequestDto createScheduleRequestDto,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();
        CreateScheduleResponseDto createScheduleResponseDto = scheduleService.create(runnerId, createScheduleRequestDto);
        log.info("[{}] [{} {}] runnerId: {} - 스케줄 생성 성공 | 응답 데이터 - scheduleId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, createScheduleResponseDto.getScheduleId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createScheduleResponseDto);
    }

    //스케줄 삭제
    @DeleteMapping("/{scheduleId}")
    @Operation(summary = "스케줄 삭제",description = "스케줄을 삭제합니다.")
    public ResponseEntity<Object> delete(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "삭제할 스케줄 ID"
            )
            @PathVariable String scheduleId,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();
        scheduleService.delete(runnerId, scheduleId);
        log.info("[{}] [{} {}] runnerId: {} - 스케줄 삭제 성공 | 요청 데이터 - scheduleId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, scheduleId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("스케줄이 성공적으로 삭제되었습니다."));
    }

    //스케줄 조회
    @GetMapping
    @Operation(summary = "스케줄 조회",description = "오늘을 입력하면, 해당 달의 스케줄들을 조회합니다.")
    public ResponseEntity<Object> getSchedules(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "검색할 Belong Id"
            )
            @RequestParam(required = false) String belongId,
            @Parameter(
                    description = "오늘의 날짜 ex) 2026-03-01"
            )
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate,
            HttpServletRequest request
    ) {
        String threadName = Thread.currentThread().getName();

        List<GetSchedulesResponseDto> schedules = scheduleService.getSchedules(runnerId, belongId, localDate);
        log.info("[{}] [{} {}] runnerId: {} - 스케줄 조회 성공 | 요청 데이터 - belongId: {}, localDate: {} | 응답 데이터 - 조회된 스케줄 개수: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, belongId, localDate, schedules.size());
        return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    //특정 그룹에서의 나의 스케줄 조회
    @GetMapping("/me")
    @Operation(summary = "나의 스케줄 조회",description = "특정 그룹에서의 스케줄을 조회합니다. 오늘을 입력하면, 해당 달의 스케줄들을 조회합니다.")
    public ResponseEntity<Object> getMySchedule(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "검색할 Group Id"
            )
            @RequestParam(required = false) String groupId,
            @Parameter(
                    description = "오늘의 날짜 ex) 2026-03-01"
            )
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate localDate,
            HttpServletRequest request
    ) {
        String threadName = Thread.currentThread().getName();

        List<GetMySchedulesResponseDto> schedules = scheduleService.getMySchedules(runnerId, groupId, localDate);
        log.info("[{}] [{} {}] runnerId: {} - 특정 그룹에서의 나의 스케줄 조회 성공 | 요청 데이터 - groupId: {}, localDate: {} | 응답 데이터 - 조회된 스케줄 개수: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, groupId, localDate, schedules.size());
        return ResponseEntity.status(HttpStatus.OK).body(schedules);
    }

    //스케줄 수정
    @PatchMapping("/{scheduleId}")
    @Operation(summary = "스케줄 수정",description = "스케줄 정보를 수정합니다.")
    public ResponseEntity<Object> revise(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "수정할 스케줄 ID"
            )
            @PathVariable String scheduleId,
            @Parameter(
                    description = "스케줄 수정 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestBody @Valid ReviseScheduleRequestDto reviseScheduleRequestDto,
            BindingResult bindingResult,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        if(bindingResult.hasErrors()){
            log.error("[{}] [{} {}] 요청 데이터 오류", threadName, request.getMethod(), request.getRequestURI());
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        ReviseScheduleResponseDto reviseScheduleResponseDto = scheduleService.revise(runnerId, scheduleId, reviseScheduleRequestDto);
        log.info("[{}] [{} {}] runnerId: {} - 스케줄 수정 성공 | 요청 데이터 - scheduleId: {} | 응답 데이터 - scheduleId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, scheduleId, reviseScheduleResponseDto.getScheduleId());
        return ResponseEntity.status(HttpStatus.OK).body(reviseScheduleResponseDto);
    }

}
