package park.brothers.runwith_back.domain.Recognize.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Recognize.dto.Request.ChangeRecognizeRequestDto;
import park.brothers.runwith_back.domain.Recognize.dto.Response.ChangeRecognizeResponseDto;
import park.brothers.runwith_back.domain.Recognize.service.RecognizeService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/recognizes")
public class RecognizeController {

    private final RecognizeService recognizeService;

    //인정하기
    @PostMapping("/{scheduleId}")
    @Operation(summary = "인정하기", description = "같은 그룹원의 스케줄을 인정합니다.")
    public ResponseEntity<Object> changeRecognize(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "인정하려는 스케줄 Id"
            )
            @PathVariable String scheduleId,
            @Parameter(
                    description = "인정을 하는지 안 하는지 여부"
            )
            @RequestBody @Valid ChangeRecognizeRequestDto changeRecognizeRequestDto,
            HttpServletRequest request
    ){
        String threadName = Thread.currentThread().getName();

        ChangeRecognizeResponseDto changeRecognizeResponseDto = recognizeService.changeRecognize(runnerId, changeRecognizeRequestDto, scheduleId);
        log.info("[{}] [{} {}] runnerId: {} - 스케줄 인정 성공 | 요청 데이터 - scheduleId: {}, 인정 여부: {} | 응답 데이터: 변경된 인정 여부: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, scheduleId, changeRecognizeRequestDto.isRecognizing(), changeRecognizeResponseDto.isRecognizing());
        return ResponseEntity.status(HttpStatus.OK).body(changeRecognizeResponseDto);
    }
}
