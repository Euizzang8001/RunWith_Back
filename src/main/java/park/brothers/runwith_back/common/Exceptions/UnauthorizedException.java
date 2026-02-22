package park.brothers.runwith_back.common.Exceptions;

//접근 불가 에러
public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message){
        super(message);
    }
}
