package park.brothers.runwith_back.domain.Runner.controller;

import com.google.firebase.auth.FirebaseAuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Request.ReviseMyInfoRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetMyInfoResponseDto;
import park.brothers.runwith_back.domain.Runner.service.RunnerService;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/runners")
public class RunnerController {

    private final RunnerService runnerService;

    //러너 저장 api
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})//요청 받는 미디어 타입을 입력해야 함
    @Operation(summary = "러너 생성",description = "새로운 러너를 생성합니다.")
    public ResponseEntity<Object> save(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "러너 생성 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestPart(value = "request") @Valid CreateRunnerRequestDto createRunnerRequestDto,
            BindingResult bindingResult,
            @Parameter(
                    description = "러너 이미지"
            )
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        CreateRunnerResponseDto createRunnerResponseDto = runnerService.save(runnerId, createRunnerRequestDto, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(createRunnerResponseDto);
    }

    //토큰으로 이미 유저가 존재하는지 확인하는 api
    @GetMapping("/me/exists")
    @Operation(summary = "러너 존재 여부 확인",description = "해당 로그인 정보로 가입한 러너가 존재하는지 확인합니다.")
    public ResponseEntity<Object> isSavedRunner(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId
    ){
        Boolean isSavedRunner = runnerService.isSavedRunner(runnerId);
        if(isSavedRunner){
            return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("존재하는 러너입니다."));
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonMessage("존재하지 않는 러너입니다."));
        }
    }

    //러너 정보 조회
    @GetMapping("/me")
    @Operation(summary = "로그인한 러너 조회",description = "로그인한 러너 정보를 조회합니다.")
    public ResponseEntity<Object> getRunner(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId
    ){
        GetMyInfoResponseDto getMyInfoResponseDto = runnerService.findRunner(runnerId);

        return ResponseEntity.status(HttpStatus.OK).body(getMyInfoResponseDto);
    }

    //러너 정보 수정
    @PatchMapping(value = "/me", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @Operation(summary = "러너 수정",description = "로그인한 러너 정보를 수정합니다.")
    public ResponseEntity<Object> reviseMyInfo(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "러너 수정 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestPart(value = "request", required = false) @Valid ReviseMyInfoRequestDto reviseMyInfoRequestDto,
            BindingResult bindingResult,
            @Parameter(
                    description = "러너 수정 이미지"
            )
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        GetMyInfoResponseDto getMyInfoResponseDto = runnerService.revise(runnerId, reviseMyInfoRequestDto, image);

        return ResponseEntity.status(HttpStatus.OK).body(getMyInfoResponseDto);
    }

    //러너 정보 삭제(탈퇴)
    @DeleteMapping(value = "/me")
    @Operation(summary = "러너 삭제", description = "로그인한 러너를 삭제(탈퇴)합니다.")
    public ResponseEntity<Object> deleteRunner(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId
    ) throws FirebaseAuthException {
        runnerService.deleteRunner(runnerId);
        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("성공적으로 탈퇴되었습니다."));
    }
}
