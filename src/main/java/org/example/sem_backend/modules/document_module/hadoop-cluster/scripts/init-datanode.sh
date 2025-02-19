# Thay thế placeholders trong file cấu hình bằng giá trị thực từ biến môi trường
sed -i "s/\${DFS_DATANODE_HOST_NAME}/$DFS_DATANODE_HOST_NAME/g" $HADOOP_CONF_DIR/hdfs-site.xml

echo "Starting Datanode..."
if hdfs datanode; then
    echo "Datanode started successfully."
else
    echo "Failed to start Datanode."
    exit 1
fi