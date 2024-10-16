FROM openjdk:21-bullseye

# Install Maven
RUN apt-get update && apt-get install -y maven make entr

# Clean up
RUN apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/*

# Set the default shell to bash
ENV SHELL /bin/bash

# Create a non-root user
RUN useradd -ms /bin/bash developer

# Set working directory
WORKDIR /workspace

# Copy the database setup script
COPY ../script.sh /workspace/script.sh
RUN chmod +x /workspace/script.sh

# Switch to non-root user
USER developer

# Set entry point to run the setup script
ENTRYPOINT ["/workspace/script.sh"]
