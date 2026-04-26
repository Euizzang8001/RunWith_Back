package park.brothers.runwith_back.common.Exceptions;

//이미지 수 규칙 오류
public class ImageLimitExceededException extends RuntimeException {
    public ImageLimitExceededException(String message) {
        super(message);
    }
}
