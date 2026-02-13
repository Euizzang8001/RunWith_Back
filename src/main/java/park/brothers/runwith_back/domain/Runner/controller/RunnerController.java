package park.brothers.runwith_back.domain.Runner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.ErrorMessage;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;
import park.brothers.runwith_back.domain.Runner.service.RunnerService;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/runners")
public class RunnerController {
    private final RunnerService runnerService;

    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody @Valid CreateRunnerRequestDto createRunnerRequestDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        Boolean isDuplication = runnerService.checkDuplication(createRunnerRequestDto);
        if(isDuplication){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorMessage("생성하고자 하는 데이터를 가진 러너들이 이미 존재합니다."));
        }
        CreateRunnerResponseDto createRunnerResponseDto = runnerService.save(createRunnerRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createRunnerResponseDto);
    }
}
