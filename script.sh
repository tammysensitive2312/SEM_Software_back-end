#!/bin/bash

# Set error handling
set -e

echo "Starting Maven build monitoring service..."

# Create a function to handle the Maven build
run_maven_build() {
    if [ -f "pom.xml" ]; then
        echo "Running Maven build..."
        mvn clean install
    else
        echo "Warning: pom.xml not found in current directory"
        sleep 5
    fi
}

# Initial build
run_maven_build

# Monitor pom.xml for changes
echo "Monitoring pom.xml for changes..."
while true; do
    if [ -f "pom.xml" ]; then
        echo "Watching pom.xml for changes..."
        ls pom.xml | entr -d sh -c 'echo "Change detected in pom.xml"; mvn clean install'
    else
        echo "Waiting for pom.xml..."
        sleep 5
    fi
done