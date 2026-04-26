package park.brothers.runwith_back.common.Error;

import com.google.firebase.auth.FirebaseAuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import park.brothers.runwith_back.common.Exceptions.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    //중복 검사
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        String runnerId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "비회원";

        log.error("[{}] [{} {}] runnerId: {} - 파일 처리 에러: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    //찾으려는 데이터가 없음
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        String runnerId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "비회원";

        log.error("[{}] [{} {}] runnerId: {} - 존재하지 않는 데이터 접근 에러: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    //찾으려는 값이 삭제하려는 값과 다름
    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<String> handleNotAcceptable(NotAcceptableException ex, HttpServletRequest request){
        String threadName = Thread.currentThread().getName();
        String runnerId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "비회원";

        log.error("[{}] [{} {}] runnerId: {} - 접근 불가한 데이터 접근 오류: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
    }

    //권한 없음
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request){
        String threadName = Thread.currentThread().getName();
        String runnerId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "비회원";

        log.error("[{}] [{} {}] runnerId: {} - 접근 불가능한 기능 접근 오류: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // 파이어베이스 오류
    @ExceptionHandler(FirebaseAuthException.class)
    public ResponseEntity<String> handleFirebaseAuthException(FirebaseAuthException ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        String runnerId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "비회원";

        log.error("[{}] [{} {}] runnerId: {} - 파이어베이스 통신 에러: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 서버 통신 중 오류가 발생했습니다.");
    }

    //입력 데이터값 오류
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        String runnerId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "비회원";

        Map<String, String> errorMap = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errorMap.put(error.getField(), error.getDefaultMessage());
        }

        String firstErrorMessage = errorMap.isEmpty() ? "입력값 오류" : errorMap.values().iterator().next();
        log.error("[{}] [{} {}] runnerId: {} - 요청 데이터 유효성 검사 실패: {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, firstErrorMessage);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }

    @ExceptionHandler(ImageLimitExceededException.class)
    public ResponseEntity<Object> handleImageLimitExceededException(ImageLimitExceededException ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        String runnerId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "비회원";

        log.error("[{}] [{} {}] runnerId: {} - 액션 생성 오류 | {}", threadName, request.getMethod(), request.getRequestURI(), runnerId, ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body(ex.getMessage());
    }

    //이외의 모드 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllException(Exception ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        String runnerId = request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "비회원";

        log.error("[{}] [{} {}] runnerId: {} - 서버 에러 발생", threadName, request.getMethod(), request.getRequestURI(), runnerId,  ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
    }
}
