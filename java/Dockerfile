FROM maven:3.6.3-jdk-11

# Set working directory
WORKDIR /usr/src/app/

# Copy source files
COPY org.travlyn.server ./org.travlyn.server
COPY pom.xml .

# Compile source files
RUN mvn clean install -DskipTests

EXPOSE 3000

# Run Travlyn server
CMD ["java"]