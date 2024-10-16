#!/bin/bash

# Kiểm tra sự thay đổi của pom.xml
echo "Keeping track of the change of pom.xml..."

# Theo dõi file pom.xml và chạy mvn clean install khi có thay đổi
while true; do
  ls pom.xml | entr -r mvn clean install
done

# Keep the container running (if necessary)
tail -f /dev/null
