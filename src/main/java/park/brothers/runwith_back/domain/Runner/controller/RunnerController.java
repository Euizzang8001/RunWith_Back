package park.brothers.runwith_back.domain.Runner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Runner.dto.CreateRunnerDto;
import park.brothers.runwith_back.domain.Runner.repository.MemoryRunnerRepository;
import park.brothers.runwith_back.domain.Runner.service.RunnerService;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/runners")
public class RunnerController {
    private final RunnerService runnerService;

    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody @Valid CreateRunnerDto createRunnerDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        runnerService.save(createRunnerDto);

        return ResponseEntity.ok(createRunnerDto);
    }
}
