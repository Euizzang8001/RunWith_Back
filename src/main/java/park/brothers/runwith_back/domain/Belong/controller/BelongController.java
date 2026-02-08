package park.brothers.runwith_back.domain.Belong.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Belong.dto.Request.ChangeLeaderRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.JoinGroupRequestDto;
import park.brothers.runwith_back.domain.Belong.dto.Request.LeaveGroupRequestDto;
import park.brothers.runwith_back.domain.Belong.service.BelongService;
import park.brothers.runwith_back.domain.Group.dto.GetGroupResponseDto;
import park.brothers.runwith_back.domain.Runner.dto.Response.GetRunnerResponseDto;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/belongs")
public class BelongController {

    private final BelongService belongService;

    //그룹 참여 api
    @PostMapping
    public ResponseEntity<Object> save(@RequestBody @Valid JoinGroupRequestDto joinGroupRequestDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        belongService.joinGroup(joinGroupRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(joinGroupRequestDto);

    }

    //그룹 탈퇴 api
    @DeleteMapping
    public ResponseEntity<Object> leave(@RequestBody @Valid LeaveGroupRequestDto leaveGroupRequestDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        belongService.leaveGroup(leaveGroupRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(leaveGroupRequestDto);
    }

    //특정 runner가 속한 모든 그룹들을 응답 받는 api
    @GetMapping("/runnerId={runnerId}")
    public ResponseEntity<Object> getAllGroupsRunnerJoin(@PathVariable @Valid Long runnerId){
        List<GetGroupResponseDto> groups = belongService.getAllGroupsRunnerJoin(runnerId);
        return ResponseEntity.status(HttpStatus.OK).body(groups);
    }

    //특정 그룹에 속한 모든 runner들을 응답 받는 api
    @GetMapping("/groupId={groupId}")
    public ResponseEntity<Object> getAllRunnersInGroup(@PathVariable @Valid Long groupId){
        List<GetRunnerResponseDto> runners = belongService.getAllRunnersInGroup(groupId);
        return ResponseEntity.status(HttpStatus.OK).body(runners);
    }

    //그룹의 리더 변경 api
    @PatchMapping("/leader")
    public ResponseEntity<Object> changeLeader(@RequestBody @Valid ChangeLeaderRequestDto changeLeaderRequestDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        belongService.changeLeader(changeLeaderRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(changeLeaderRequestDto);
    }






}
