# Hướng dẫn Khởi tạo Môi trường Phát triển

Để bắt đầu phát triển, hãy làm theo các bước sau để thiết lập môi trường của bạn:
- hãy chắc chắn rằng bạn đã cài đặt plugin makefile ở trong ide của bạn 

## Bước 1: Chạy môi trường phát triển

Chạy lệnh sau để khởi động môi trường phát triển:

```sh
make setup-dev-env
```
Có thể thay thế bằng câu lệnh:
``` sh
docker-compose up -d
```

## Bước 2: Truy cập vào container phát triển

Sau khi môi trường đã được khởi động, truy cập vào container phát triển bằng lệnh:

```sh
docker exec -it sembackend-devcontainer-1 /bin/bash
```

## Bước 3: Chạy migration

Trong container phát triển, chạy lệnh sau để tạo các bảng và dữ liệu ban đầu:

``` sh
make run-migration
```

Sau khi hoàn thành các bước trên, môi trường phát triển của bạn đã sẵn sàng để sử dụng.

---