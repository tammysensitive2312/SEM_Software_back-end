# Tổng quan Kiến trúc Hệ thống

## Sơ đồ Kiến trúc

![Kiến trúc Hệ thống](https://github.com/user-attachments/assets/8ceb4d5e-d0fd-415a-820f-9f1714e07209)


## Mô tả Kiến trúc

Hệ thống Sem Backend được thiết kế theo mô hình modular-monolithic, với các đặc điểm chính sau:

1. **Main-service** đóng vai trò như một API Gateway, điều phối các yêu cầu đến các module tương ứng.

2. Các service và repository của các module được mount vào các container tương ứng, cho phép tính modular và khả năng mở rộng.

3. Việc phát triển giữa các container là độc lập, tạo điều kiện cho việc phát triển và bảo trì song song.

4. Khi cần tạo bảng mới, các developer nên thực hiện migration trong thư mục resources của module tương ứng.

## Các Module Chính

- Common Module
- Room Module
- Equipment Module
- User Management Module
- Borrowing Management Module
- Incident Reporting Module
- Notification Module

## Luồng Dữ liệu

- Frontend giao tiếp với hệ thống thông qua main-service.
- Main-service định tuyến yêu cầu đến các module phù hợp.
- Các module tương tác với cơ sở dữ liệu MySQL thông qua các repository.
- Borrowing Management Module đóng vai trò là producer cho hệ thống messaging.
- Notification Module có thể đóng vai trò là consumer cho hệ thống messaging.

## Lưu ý cho Developers

- Tuân thủ cấu trúc modular khi phát triển tính năng mới.
- Thực hiện migration cho các thay đổi cơ sở dữ liệu trong thư mục resources của module tương ứng.
- Duy trì tính độc lập giữa các container để đảm bảo khả năng mở rộng và bảo trì trong tương lai.