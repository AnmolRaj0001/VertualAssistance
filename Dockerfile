FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

# ✅ Make mvnw executable
RUN chmod +x mvnw

# Build the app
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/VertualAssistance-0.0.1-SNAPSHOT.jar"]
FROM openjdk:17-jdk-slim

WORKDIR /app

COPY . .

# ✅ Make mvnw executable
RUN chmod +x mvnw

# Build the app
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/VertualAssistance-0.0.1-SNAPSHOT.jar"]
