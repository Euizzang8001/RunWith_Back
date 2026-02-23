package park.brothers.runwith_back.common.Exceptions;

//조회하려는 값이 없을 때 return
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}