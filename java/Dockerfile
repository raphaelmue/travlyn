FROM maven:3.6.3-jdk-11

# Set working directory
WORKDIR /usr/src/app/

# Copy source files
COPY org.travlyn.server ./org.travlyn.server
COPY org.travlyn.shared ./org.travlyn.shared
COPY org.travlyn.util ./org.travlyn.util
COPY pom.xml .

# Compile source files
RUN mvn clean install -DskipTests

EXPOSE 3000

# Run Travlyn server
CMD ["java", "-jar", "org.travlyn.server/target/org.travlyn.server-1.0-SNAPSHOT.jar"]