package park.brothers.runwith_back.domain.Runner.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.domain.Runner.entity.Runner;
import park.brothers.runwith_back.domain.Runner.repository.MemoryRunnerRepository;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/runners")
public class RunnerController {
    private final MemoryRunnerRepository runnerRepository;

    @PostMapping("/add")
    public ResponseEntity<Object> save(@RequestBody @Valid Runner runner, BindingResult result) {
        if (result.hasErrors()) {
            log.info("validation errors={}", result);

            Map<String, String> errors = new HashMap<>();
            for (FieldError error : result.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
        }

        runnerRepository.save(runner);

        return ResponseEntity.ok(runner);
    }
}
