# --- Giai đoạn 1: Build dự án ---
FROM maven:3.9.4-eclipse-temurin-23 AS build
WORKDIR /app

# Copy file cấu hình Maven trước để cache dependencies
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy mã nguồn và đóng gói thành file .jar
COPY src ./src
RUN mvn clean package -DskipTests

# --- Giai đoạn 2: Chạy ứng dụng ---
FROM eclipse-temurin:23-jdk-jammy
WORKDIR /app

# Copy file jar đã build từ giai đoạn trước
COPY --from=build /app/target/*.jar app.jar

# Thông báo cổng (Render sẽ tự động quản lý qua biến môi trường $PORT)
EXPOSE 8080

# Lệnh chạy ứng dụng
ENTRYPOINT ["java", "-Xmx384m", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]
