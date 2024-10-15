#!/bin/bash

# Wait for MySQL to be ready
until mysql -h"$MYSQL_HOST" -u"$MYSQL_USER" -p"$MYSQL_PASSWORD" -e "SHOW DATABASES;" &> /dev/null
do
    echo "Waiting for MySQL to be ready..."
    sleep 1
done

echo "Database setup completed."

# Keep the container running
tail -f /dev/null
