package park.brothers.runwith_back.domain.JoinRequest.controller;

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
import park.brothers.runwith_back.common.CommonMessage;
import park.brothers.runwith_back.common.Response.ValidationErrorUtils;
import park.brothers.runwith_back.domain.JoinRequest.dto.request.CreateJoinRequestRequestDto;
import park.brothers.runwith_back.domain.JoinRequest.dto.response.CreateJoinRequestResponseDto;
import park.brothers.runwith_back.domain.JoinRequest.dto.response.GetJoinRequestResponseDto;
import park.brothers.runwith_back.domain.JoinRequest.dto.response.GetMyJoinRequestsResponseDto;
import park.brothers.runwith_back.domain.JoinRequest.service.JoinRequestService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/join-requests")
public class JoinRequestController {

    private final JoinRequestService joinRequestService;

    //그룹 가입 신청 생성 api
    @PostMapping
    @Operation(summary = "그룹 가입 신청 생성",description = "그룹에 가입 신청합니다.")
    public ResponseEntity<Object> save(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @Parameter(
                    description = "그룹 가입 신청 정보",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)
            )
            @RequestBody @Valid CreateJoinRequestRequestDto createJoinRequestRequestDto,
            BindingResult bindingResult
    ) throws IOException { //BindingResult은 DTO만
        if (bindingResult.hasErrors()) {
            return ValidationErrorUtils.handleValidationErrors(bindingResult);
        }

        CreateJoinRequestResponseDto createJoinRequestResponseDto = joinRequestService.save(runnerId, createJoinRequestRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(createJoinRequestResponseDto);
    }

    //그룹 가입 신청 삭제 api
    @DeleteMapping("/{joinRequestId}")
    @Operation(summary = "그룹 가입 신청 삭제",description = "신청한 그룹 가입을 삭제합니다.")
    public ResponseEntity<Object> deleteJoinRequest(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @PathVariable String joinRequestId
    ){
        joinRequestService.delete(runnerId, joinRequestId);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("신청한 그룹 가입이 삭제되었습니다."));
    }

    //그룹 가입 신청 승인 api(리더)
    @PostMapping("/{joinRequestId}/accept")
    @Operation(summary = "그룹 신청 승인", description = "리더가 그룹 가입 신청 하나를 승인합니다.")
    public ResponseEntity<Object> acceptJoinRequest(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @PathVariable String joinRequestId
    ){
        joinRequestService.accept(runnerId, joinRequestId);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("성공적으로 승인되었습니다."));
    }

    //그룹 가입 신청 거부 api(리더)
    @PostMapping("/{joinRequestId}/reject")
    @Operation(summary = "그룹 신청 거절", description = "리더가 그룹 가입 신청 하나를 거절합니다.")
    public ResponseEntity<Object> rejectJoinRequest(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @PathVariable String joinRequestId
    ){
        joinRequestService.reject(runnerId, joinRequestId);

        return ResponseEntity.status(HttpStatus.OK).body(new CommonMessage("성공적으로 거절되었습니다."));
    }

    //그룹 가입 신청 명단 보기(리더)
    @GetMapping("/groups/{groupId}")
    @Operation(summary = "그룹 신청 명단 보기", description = "리더가 그룹 신청 명단을 조회합니다.")
    public ResponseEntity<Object> getJoinRequestList(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId,
            @PathVariable String groupId
    ){
        List<GetJoinRequestResponseDto> getJoinRequestResponseDtos = joinRequestService.getJoinRequestOfGroup(runnerId, groupId);

        return ResponseEntity.status(HttpStatus.OK).body(getJoinRequestResponseDtos);
    }

    //내 가입 신청 보기
    @GetMapping("/me")
    @Operation(summary = "나의 신청 보기", description = "내가 신청한 그룹 신청 명단을 조회합니다.")
    public  ResponseEntity<Object> getMyJoinRequest(
            @Parameter(hidden = true) @AuthenticationPrincipal String runnerId
    ){
        List<GetMyJoinRequestsResponseDto> getMyJoinRequestsResponseDtos = joinRequestService.getMyJoinRequest(runnerId);

        return ResponseEntity.status(HttpStatus.OK).body(getMyJoinRequestsResponseDtos);
    }


}
