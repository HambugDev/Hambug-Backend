# 빌드 스테이지
FROM --platform=$BUILDPLATFORM eclipse-temurin:17-jdk-focal AS build
WORKDIR /app

# Gradle 래퍼와 설정 파일 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x gradlew

# 종속성 미리 다운로드 (캐시 활용)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 및 리소스 복사
COPY src src

# 애플리케이션 빌드 (테스트 제외)
RUN ./gradlew bootJar -x test --no-daemon

# 실행 스테이지
FROM eclipse-temurin:17-jre-focal
ARG TARGETPLATFORM
RUN echo "Building for $TARGETPLATFORM"
WORKDIR /app

# 빌드 스테이지에서 생성된 jar 파일 복사
# build.gradle에서 archiveFileName을 app.jar로 설정했으므로 그대로 복사
COPY --from=build /app/build/libs/app.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# 포트 설정
EXPOSE 8080
