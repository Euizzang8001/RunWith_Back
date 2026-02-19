package park.brothers.runwith_back.external.AWS_S3;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class AWSS3Service {

    private final S3Client s3Client;

    private final S3Presigner s3Presigner;

    // aws s3의 presigned url이 담긴 캐시 맵
    private final Map<String, CachedUrlInfo> urlCacheMap = new ConcurrentHashMap<>();

    @Value("${spring.cloud.aws.s3.bucket.name}")
    private String bucketName;

    //이미지 업로드하고, 리턴값으로 presignedurl얻기
    public String putImageToAWSS3(
            MultipartFile image,
            String imageType,
            Long id,
            int sequence
    ) throws IOException {
        //파일을 png로 통일
        byte[] imageByte = convertToPNG(image);

        String imageName = "%s/id=%dsequence=%d.png".formatted(imageType, id, sequence);

        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(imageName)
                        .build(),
                RequestBody.fromBytes(imageByte)
        );

        //받을 image객체
        return getPresignedImageUrl(imageName);

    }

    //image의 presignedUrl 얻기
    public String getImagePresignedUrl(String imageType, Long id, int sequence) {

        String imageName = "%s/id=%dsequence=%d.png".formatted(imageType, id, sequence);

        //캐시맵에 저장되어 있고 만료되지 않았다면, 캐시에서 가져오기
        if(urlCacheMap.containsKey(imageName)){
            CachedUrlInfo cachedUrlInfo = urlCacheMap.get(imageName);

            //캐시 만료 1분전까지만 유효하도록 설정
            if(cachedUrlInfo.expiredTime.isAfter(Instant.now().plusSeconds(60))){
                return cachedUrlInfo.url;
            }
            else{ //캐시 만료시 캐시 제거
                urlCacheMap.remove(imageName);
            }
        }

        //다운로드할 객체 설정
        return getPresignedImageUrl(imageName);
    }

    //이미지의 presigned url 추출하기
    private String getPresignedImageUrl(String imageName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(imageName)
                .build();

        //PreSigned URL 요청
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10)) //만료 시간 설정
                .getObjectRequest(getObjectRequest)
                .build();

        //발급된 URL
        PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
        String stringPresignedRequestUrl = presignedRequest.url().toString();

        //캐시 저장
        urlCacheMap.put(imageName, new CachedUrlInfo(stringPresignedRequestUrl, Instant.now()));

        //리턴하기
        return stringPresignedRequestUrl;
    }

    //캐시에 저장할 객체
    public record CachedUrlInfo(String url, Instant expiredTime) {

    }

    //이미지는 png로 통일
    private byte[] convertToPNG(MultipartFile originalImage) throws IOException{
        //원본 이미지 읽기
        BufferedImage bufferedImage = ImageIO.read(originalImage.getInputStream());

        // PNG로 변환하여 쓰기
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "png", byteArrayOutputStream);
        byteArrayOutputStream.flush();

        //png로 변환한 값 리턴
        return byteArrayOutputStream.toByteArray();
    }
}
