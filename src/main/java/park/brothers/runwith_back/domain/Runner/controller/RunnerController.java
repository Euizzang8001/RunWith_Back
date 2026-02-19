package park.brothers.runwith_back.domain.Runner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;
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
            @RequestPart(value = "request") @Valid CreateRunnerRequestDto createRunnerRequestDto,
            BindingResult bindingResult,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        Boolean isDuplication = runnerService.checkDuplication(createRunnerRequestDto);
        if(isDuplication){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new CommonMessage("생성하고자 하는 데이터를 가진 러너들이 이미 존재합니다."));
        }

        CreateRunnerResponseDto createRunnerResponseDto = runnerService.save(createRunnerRequestDto, image);

        return ResponseEntity.status(HttpStatus.CREATED).body(createRunnerResponseDto);
    }
}
