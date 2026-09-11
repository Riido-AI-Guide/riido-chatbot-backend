# ---- build ----
FROM eclipse-temurin:25-jdk AS build
WORKDIR /workspace

# 빌드 스크립트만 먼저 복사해 의존성을 받아둔다 — 소스만 바뀌면 이 레이어는 캐시를 탄다
COPY gradlew settings.gradle.kts build.gradle.kts ./
COPY gradle gradle
RUN ./gradlew dependencies --no-daemon > /dev/null

COPY src src
# bootJar만 돌리면 -plain.jar 없이 실행 jar 하나만 나온다
RUN ./gradlew bootJar --no-daemon \
    && mv build/libs/*.jar application.jar \
    && java -Djarmode=tools -jar application.jar extract --layers --destination extracted

# ---- runtime ----
FROM eclipse-temurin:25-jre
WORKDIR /application

RUN groupadd --system spring && useradd --system --gid spring spring

# 자주 바뀌지 않는 순서대로 복사해 이미지 레이어 캐시를 살린다
COPY --from=build /workspace/extracted/dependencies/ ./
COPY --from=build /workspace/extracted/spring-boot-loader/ ./
COPY --from=build /workspace/extracted/snapshot-dependencies/ ./
COPY --from=build /workspace/extracted/application/ ./

USER spring
EXPOSE 8080

# 컨테이너 메모리 한도 기준으로 힙을 잡는다. Container Apps 환경변수로 덮어쓸 수 있다
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["java", "-jar", "application.jar"]
