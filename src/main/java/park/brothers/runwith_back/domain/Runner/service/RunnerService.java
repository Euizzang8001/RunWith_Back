package park.brothers.runwith_back.domain.Runner.service;

import jakarta.validation.Valid;
import park.brothers.runwith_back.domain.Runner.dto.Request.CreateRunnerRequestDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.CreateRunnerResponseDto;

public interface RunnerService {
    CreateRunnerResponseDto save(CreateRunnerRequestDto createRunnerRequestDto) throws IllegalAccessError;

    Boolean checkDuplication(@Valid CreateRunnerRequestDto createRunnerRequestDto);
}
