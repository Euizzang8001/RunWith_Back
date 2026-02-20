package park.brothers.runwith_back.domain.Action.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.Action.dto.Request.CreateActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Request.ReviseActionRequestDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetActionsResponseDto;
import park.brothers.runwith_back.domain.Action.dto.Response.GetOneActionResponseDto;
import park.brothers.runwith_back.domain.Action.service.ActionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/actions")
public class ActionController {

    private final ActionService actionService;

    //Action 생성
    @PostMapping
    public ResponseEntity<Object> createAction(@RequestBody @Valid CreateActionRequestDto createActionRequestDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        actionService.createAction(createActionRequestDto);
        return ResponseEntity.status(HttpStatus.OK).body(createActionRequestDto);
    }

    //조건에 맞는 Actions 조회
    @GetMapping
    public ResponseEntity<Object>  getActionsByScheduleId(
            @RequestParam(required = false) @Valid String scheduleId
    ){
           List<GetActionsResponseDto> getActionsResponseDto = actionService.getActionsByScheduleId(scheduleId);

           return ResponseEntity.status(HttpStatus.OK).body(getActionsResponseDto);
    }

    //id로 하나의 Action조회
    @GetMapping("/detail")
    public ResponseEntity<Object> getOneActionById(
        @RequestParam(required = false) @Valid String id
    ){
        GetOneActionResponseDto getOneActionResponseDto = actionService.getActionById(id);
        return ResponseEntity.status(HttpStatus.OK).body((getOneActionResponseDto));
    }



    //Action 삭제
    @DeleteMapping
    public ResponseEntity<Object> deleteAction(@RequestParam @Valid String id){
        actionService.deleteAction(id);

        return ResponseEntity.status(HttpStatus.OK).body(id);
    }

    //Action수정
    @PatchMapping
    public ResponseEntity<Object> reviseAction(@RequestBody @Valid ReviseActionRequestDto reviseActionRequestDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        actionService.reviseAction(reviseActionRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(reviseActionRequestDto);
    }

}
