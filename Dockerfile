# 1. 빌드 단계
# 빌드를 위한 기본 베이스로 JDK 17 사용
# 이 단계를 이후에 다시 참조할 수 있도록 builder라는 이름으로 설정
FROM eclipse-temurin:17-jdk AS builder

# WORKDIR은 컨테이너 내부의 작업 디렉토리를 설정하는 것으로, 이후 명령어는 이 디렉토리에서 실행된다.
# 이 서버에서는 /app으로 설정
WORKDIR /app

# Gradle 래퍼와 의존성 관련 파일을 복사하며, 빌드 설정 파일들을 세팅한다.
# 소스 코드보다 먼저 복사함으로써, 소스 코드가 변경되어도 캐시를 통해 빌드 속도를 높인다.
COPY gradlew gradlew.bat build.gradle settings.gradle ./
COPY gradle ./gradle

# 프로젝트에 필요한 라이브러리(의존성)을 다운로드한다.
# --no-daemon으로, 빌드 후 Gradle 데몬 프로세스를 남겨두지 않음으로써, 메모리를 아낀다.
RUN ./gradlew dependencies --no-daemon

# 실제 백엔드 소스 코드(src)를 /src폴더에 복사한다.
COPY src ./src

# 프로젝트 이미지화에 필요한 JAR 파일 만들기
# clean: 이전 빌드 결과물 삭제
# build: 빌드 수행
# -x test: 빌드 시간 단축 및 환경 의존성 문제 방지를 위해 테스트 코드는 실행하지 않음
# --no-daemon으로, JAR파일을 만든 후 gradlew 데몬 프로세스를 남겨두지 않으면서 메모리를 아낀다.
RUN ./gradlew clean build -x test --no-daemon


# 2. 실행 단계
# 실행 단계로, JDK보다 가벼운 JRE로 수행
# jammy: OS 버전(Ubuntu 22.04 LTS)을 의미
FROM eclipse-temurin:17-jre-jammy

# 작업 디렉토리를 /app으로 설정
WORKDIR /app

# 비루트 사용자 생성 및 설정
# 보안을 위한 명령어로, 해킹을 당하더라도 시스템 전체를 장악당하지 않게 하는 보안 조치
RUN groupadd --system appgroup && useradd --system --gid appgroup appuser

# 위에서 생성한 비루트 사용자의 권한으로 실행하겠다 선언
USER appuser

# builder 스테이지(1단계)에서 생성된 파일들 중 JAR파일만 뽑아서 현재 단계에 app.jar이름으로 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 이 컨테이너의 포트가 32108임을 설정
EXPOSE 32108

# 컨테이너가 시작될 때 실행할 명령어를 설정
# java -jar app.jar를 실행
# -Duser.timezone: 시간을 '한국 표준시(KST)'로 맞춤
ENTRYPOINT ["java", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]