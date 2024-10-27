FROM openjdk:21-bullseye

RUN apt-get update && apt-get install -y maven make entr

RUN apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/*

ENV SHELL /bin/bash

RUN useradd -ms /bin/bash developer

RUN mkdir -p /workspace && \
    chown -R developer:developer /workspace

RUN mvn dependency:get -Dartifact=org.flywaydb:flyway-maven-plugin:8.5.13

WORKDIR /workspace

COPY script.sh /workspace/script.sh

# Cấp quyền đúng cho script
RUN chown developer:developer /workspace/script.sh && \
    chmod +x /workspace/script.sh

# Chuyển sang user không phải root
USER developer

# Đặt entry point
ENTRYPOINT ["/bin/bash", "/workspace/script.sh"]
