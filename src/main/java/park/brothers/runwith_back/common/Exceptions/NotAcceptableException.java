package park.brothers.runwith_back.common.Exceptions;

//찾은 값과 타깃 값이 다를 때
public class NotAcceptableException extends RuntimeException{
    public NotAcceptableException(String message){
        super(message);
    }
}
