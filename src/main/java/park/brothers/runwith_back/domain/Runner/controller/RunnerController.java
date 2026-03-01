package park.brothers.runwith_back.domain.Runner.controller;

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

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})//요청 받는 미디어 타입을 입력해야 함
    public ResponseEntity<Object> save(
            @AuthenticationPrincipal String runnerId,
            @RequestPart(value = "request") @Valid CreateRunnerRequestDto createRunnerRequestDto,
            BindingResult bindingResult,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        CreateRunnerResponseDto createRunnerResponseDto = runnerService.save(runnerId, createRunnerRequestDto, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(createRunnerResponseDto);
    }

    //토큰으로 이미 유저가 존재하는지 확인하는 api
    @GetMapping("/exist")
    public ResponseEntity<Object> isSavedRunner(
            @AuthenticationPrincipal @Valid String runnerId
    ){
        Boolean isSavedRunner = runnerService.isSavedRunner(runnerId);
        if(isSavedRunner){
            return ResponseEntity.status(HttpStatus.FOUND).body(new CommonMessage("존재하는 러너입니다."));
        } else{
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new CommonMessage("존재하지 않는 러너입니다."));
        }
    }

    //러너 정보 조회
    @GetMapping
    public ResponseEntity<Object> getRunner(
            @AuthenticationPrincipal @Valid String runnerId
    ){
        GetMyInfoResponseDto getMyInfoResponseDto = runnerService.findRunner(runnerId);

        return ResponseEntity.status(HttpStatus.OK).body(getMyInfoResponseDto);
    }
}
