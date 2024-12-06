# 1. 기본 이미지 선택 (Java 런타임 환경 제공)
FROM openjdk:17-jdk-alpine

# 2. 시간대 설정(서울)
ENV TZ=Asia/Seoul

RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 3. 로그 디렉토리 생성 및 권한 설정
RUN mkdir -p /var/log/finance && \
    chmod -R 755 /var/log/finance && \
    chown -R root:root /var/log/finance

# 4. Gradle 빌드
CMD ["./gradlew", "clean", "build"]

# 5. 애플리케이션 JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 6. 환경 변수 설정 (기본값)
ENV SPRING_PROFILES_ACTIVE=api

# 7. 컨테이너에서 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 8. 컨테이너에서 사용하는 포트 설정
EXPOSE 8080

# API 서버 : docker run -e SPRING_PROFILES_ACTIVE=api -p 8080:8080 springboot-api
# 스케줄러 서버 : docker run -e SPRING_PROFILES_ACTIVE=scheduler -p 8080:8080 springboot-scheduler