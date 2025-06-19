# Use official OpenJDK image
FROM openjdk:17-jdk-slim

# Set working directory inside container
WORKDIR /app

# Copy everything
COPY . .

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the app
CMD ["java", "-jar", "target/VertualAssistance-0.0.1-SNAPSHOT.jar"]
