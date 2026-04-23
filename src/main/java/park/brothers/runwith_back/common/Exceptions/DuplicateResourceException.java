package park.brothers.runwith_back.common.Exceptions;

//중복 발생시 return
public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}