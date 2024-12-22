# 빌드 스테이지
# 1. 기본 이미지 선택 (Java 런타임 환경 제공)
FROM openjdk:17-jdk-alpine as builder
WORKDIR /app

# Gradle Wrapper 복사
COPY gradlew .
COPY gradle gradle
RUN chmod +x ./gradlew

# 의존성 파일 복사 및 다운로드
COPY build.gradle.kts .
COPY settings.gradle.kts .
RUN ./gradlew --no-daemon dependencies

# 소스코드 복사 및 애플리케이션 빌드
COPY . .
RUN ./gradlew --no-daemon clean build -x test --stacktrace

# 실행 스테이지
FROM openjdk:17-jdk-alpine

# 0. jar 파일(실행파일) 복사
COPY --from=builder /app/build/libs/*.jar /app/app.jar

# 1. 시간대 설정(서울)
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 2. 로그 디렉토리 생성 및 권한 설정
RUN mkdir -p /var/log/finance && \
    chmod -R 755 /var/log/finance && \
    chown -R root:root /var/log/finance

# 3. 환경 변수 설정 (기본값)
ENV SPRING_PROFILES_ACTIVE=api

# 4. 컨테이너에서 애플리케이션 실행
ENTRYPOINT ["java"]
CMD ["-jar", "/app/app.jar"]

# 5. 컨테이너에서 사용하는 포트 설정
EXPOSE 8080
EXPOSE 8000

# API 서버 : docker run -e SPRING_PROFILES_ACTIVE=api -p 8080:8080 --name springboot-api ghcr.io/jmsuper/finance:번호
# 스케줄러 서버 : docker run -e SPRING_PROFILES_ACTIVE=scheduler -p 8080:8080 springboot-scheduler