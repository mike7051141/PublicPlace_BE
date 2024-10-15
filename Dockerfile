# 1. 베이스 이미지 선택
FROM openjdk:17-jdk-alpine

# 2. 작업 디렉토리 설정
WORKDIR /app

# 3. 빌드된 JAR 파일을 컨테이너로 복사
COPY build/libs/publicplace-0.0.1-SNAPSHOT.jar app.jar

# 4. 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]

# 5. 포트 노출 (필요 시)
EXPOSE 8080