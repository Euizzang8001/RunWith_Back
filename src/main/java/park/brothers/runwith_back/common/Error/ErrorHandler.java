package park.brothers.runwith_back.common.Error;

import com.google.firebase.auth.FirebaseAuthException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import park.brothers.runwith_back.common.Exceptions.DuplicateResourceException;
import park.brothers.runwith_back.common.Exceptions.NotAcceptableException;
import park.brothers.runwith_back.common.Exceptions.ResourceNotFoundException;
import park.brothers.runwith_back.common.Exceptions.UnauthorizedException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    //중복 검사
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResource(DuplicateResourceException ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        log.error("[{}] [{} {}] 파일 처리 에러: {}", threadName, request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    //찾으려는 데이터가 없음
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        log.error("[{}] [{} {}] 존재하지 않는 데이터 접근 에러: {}", threadName, request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    //찾으려는 값이 삭제하려는 값과 다름
    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<String> handleNotAcceptable(NotAcceptableException ex, HttpServletRequest request){
        String threadName = Thread.currentThread().getName();
        log.error("[{}] [{} {}] 접근 불가한 데이터 접근 오류: {}", threadName, request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(ex.getMessage());
    }

    //권한 없음
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handleUnauthorized(UnauthorizedException ex, HttpServletRequest request){
        String threadName = Thread.currentThread().getName();
        log.error("[{}] [{} {}] 접근 불가능한 기능 접근 오류: {}", threadName, request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // 파이어베이스 오류
    @ExceptionHandler(FirebaseAuthException.class)
    public ResponseEntity<String> handleFirebaseAuthException(FirebaseAuthException ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        log.error("[{}] [{} {}] 파이어베이스 통신 에러: {}", threadName, request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증 서버 통신 중 오류가 발생했습니다.");
    }

    //이외의 모드 에러 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllException(Exception ex, HttpServletRequest request) {
        String threadName = Thread.currentThread().getName();
        log.error("[{}] [{} {}] 서버 에러 발생", threadName, request.getMethod(), request.getRequestURI(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다. 잠시 후 다시 시도해 주세요.");
    }
}
