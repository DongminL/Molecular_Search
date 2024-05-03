# 사용할 JDK
FROM openjdk:17-oracle

# Working Directory 생성
WORKDIR /app

# jar 파일을 Docker Container로 복사
COPY /build/libs/canchem.jar /app/canchem.jar

# jar 파일 실행
ENTRYPOINT ["java", "-jar", "/app/canchem.jar"]