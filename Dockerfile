# Build stage
FROM maven:3.8.3-slim AS builder

# Cài đặt flyway plugin trong builder stage
RUN mvn dependency:get -Dartifact=org.flywaydb:flyway-maven-plugin:8.5.13

# Development stage
FROM eclipse-temurin:21-jre-jammy

# Cài đặt các package cần thiết cho development
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
    make \
    entr \
    git \
    curl \
    wget \
    vim \
    maven && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Tạo non-root user
RUN useradd -ms /bin/bash developer

# Tạo và set quyền cho workspace
RUN mkdir -p /workspace && \
    chown -R developer:developer /workspace

# Copy maven dependencies từ builder stage
COPY --from=builder /root/.m2 /home/developer/.m2

# Set workspace
WORKDIR /workspace

# Set environment variables
ENV SHELL=/bin/bash \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Add healthcheck
HEALTHCHECK --interval=30s --timeout=3s \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# Switch to non-root user
USER developer

# Mount points cho source code và maven dependencies
VOLUME ["/workspace", "/home/developer/.m2"]

# Expose ports cho development
EXPOSE 8080

# Set entry point
ENTRYPOINT ["tail", "-f", "/dev/null"]