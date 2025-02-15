echo "Starting Datanode..."
if hdfs datanode; then
    echo "Datanode started successfully."
else
    echo "Failed to start Datanode."
    exit 1
fi