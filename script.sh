#!/bin/bash

# Step 1: Khởi động container MySQL từ docker-compose
echo "Starting MySQL container..."
docker-compose up -d db

# Step 2: Chờ cho MySQL sẵn sàng
echo "Waiting for MySQL to be ready..."
until docker exec -it $(docker-compose ps -q db) mysql -h"$MYSQL_HOST" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SHOW DATABASES;" &> /dev/null
do
    echo "Waiting for MySQL to be ready..."
    sleep 1
done

echo "MySQL is ready and databases are accessible."

# Keep the container running (if necessary)
tail -f /dev/null
