# 1. 기본 이미지 선택 (Java 런타임 환경 제공)
FROM openjdk:17-jdk-alpine

# 2. 시간대 설정(서울)
ENV TZ=Asia/Seoul

RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone

# 3. gradle 빌드
CMD ["./gradlew", "clean", "build"]

VOLUME /tmp

# 3. 애플리케이션 JAR 파일 복사
ARG JAR_FILE=build/libs/*.jar
COPY ${JAR_FILE} app.jar

# 4. 컨테이너에서 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app.jar"]

# 5. 컨테이너에서 사용하는 포트 설정
EXPOSE 8080