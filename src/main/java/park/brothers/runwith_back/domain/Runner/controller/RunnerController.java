package park.brothers.runwith_back.domain.Runner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.service.RunnerService;

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

        runnerService.save(createRunnerRequestDto);

        return ResponseEntity.ok(createRunnerRequestDto);
    }
}
