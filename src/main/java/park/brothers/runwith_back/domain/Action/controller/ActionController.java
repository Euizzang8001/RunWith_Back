package park.brothers.runwith_back.domain.Action.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.common.Exceptions.ImageLimitExceededException;
import park.brothers.runwith_back.domain.Action.dto.Request.CreateActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Request.ReviseActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Response.CreateActionResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetActionsResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetOneActionResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.ReviseActionResponseDto;
import park.brothers.runwith_back.domain.Action.service.ActionService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/actions")
public class ActionController {

    private final ActionService actionService;

    //Action 생성
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "Action 생성", description = "Action을 생성합니다.")
    public ResponseEntity<Object> createAction(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "Action 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestPart(value = "request") @Valid CreateActionRequestDto createActionRequestDto,
            @Parameter(
                    description = "액션 이미지 목록 (최대 5장)",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
                    )
            )
            @RequestPart(value = "image", required = false) List<MultipartFile> images,
            HttpServletRequest request
    ) throws IOException {
        String threadName = Thread.currentThread().getName();

        //이미지 5장 초과면 경고
        if (images != null && images.size() > 5){
            throw new ImageLimitExceededException("이미지는 최대 5장까지입니다.");
        }

        CreateActionResponseDto createActionResponseDto = actionService.createAction(runnerId, createActionRequestDto, images == null ? new ArrayList<>() : images) ;
        log.info("[{}] [{} {}] runnerId: {} - 액션 생성 성공 | 응답 데이터 - actionId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, createActionResponseDto.getActionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(createActionResponseDto);
    }

    //조건에 맞는 Actions 조회
    @GetMapping
    @Operation(summary = "Action들 조회", description = "ScheduleId에 속한 여러 액션들을 조회합니다.")
    public ResponseEntity<Object>  getActionsByScheduleId(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "Schedule Id"
            )
            @RequestParam String scheduleId,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        List<GetActionsResponseDto> getActionsResponseDto = actionService.getActionsByScheduleId(scheduleId);
        log.info("[{}] [{} {}] runnerId: {} - 액션 조회 성공 | 요청 데이터 - scheduleId: {} | 응답 데이터 - 조회된 Action 수: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, scheduleId, getActionsResponseDto.size());
        return ResponseEntity.status(HttpStatus.OK).body(getActionsResponseDto);
    }

    //id로 하나의 Action조회
    @GetMapping("/{actionId}")
    @Operation(summary = "하나의 Action 상세 보기", description = "특정 Action하나를 상세 보기 합니다.")
    public ResponseEntity<Object> getOneActionById(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @PathVariable String actionId,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        GetOneActionResponseDto getOneActionResponseDto = actionService.getActionById(actionId);
        log.info("[{}] [{} {}] runnerId: {} - 액션 하나 상세 조회 성공 | 요청 데이터 - actionId: {} | 응답 데이터 - actionId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, actionId, getOneActionResponseDto.getActionId());

        return ResponseEntity.status(HttpStatus.OK).body(getOneActionResponseDto);
    }



    //Action 삭제
    @DeleteMapping("/{actionId}")
    @Operation(summary = "액션 삭제", description = "하나의 액션을 삭제합니다.")
    public ResponseEntity<Object> deleteAction(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @PathVariable String actionId,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        actionService.deleteAction(runnerId, actionId);
        log.info("[{}] [{} {}] runnerId: {} - 액션 삭제 성공 | 요청 데이터 - actionId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, actionId);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("액션 삭제가 완료되었습니다."));
    }

    //Action수정
    @PatchMapping(path = "/{actionId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "하나의 Action 수정하기", description = "특정 Action하나를 수정합니다.")
    public ResponseEntity<Object> reviseAction(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @PathVariable String actionId,
            @Parameter(
                    description = "수정할 Action 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestPart(value = "request") @Valid ReviseActionRequestDto reviseActionRequestDto,
            @Parameter(
                    description = "액션 이미지 목록 (최대 5장)",
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            array = @ArraySchema(schema = @Schema(type = "string", format = "binary"))
                    )
            )
            @RequestPart(value = "image", required = false) List<MultipartFile> images,
            HttpServletRequest request
    ) throws IOException {
        String threadName = Thread.currentThread().getName();

        //이미지 5장 초과면 경고
        if (images != null && images.size() > 5){
            throw new ImageLimitExceededException("이미지는 최대 5장까지입니다.");
        }

        ReviseActionResponseDto reviseActionResponseDto = actionService.reviseAction(runnerId, actionId, reviseActionRequestDto, images == null ? new ArrayList<>() : images);
        log.info("[{}] [{} {}] runnerId: {} - 액션 수정 성공 | 요청 데이터 - actionId: {} | 응답 데이터 - actionId: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, actionId, reviseActionResponseDto.getActionId());

        return ResponseEntity.status(HttpStatus.OK).body(reviseActionResponseDto);
    }

}
