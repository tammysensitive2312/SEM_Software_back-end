# Use case chức năng quản lý tài liệu

## 1. Mô tả

Người dùng có thể sử dụng tính năng này để tra cứu tìm kiếm tài liệu nội bộ của tổ chức, 
bên cạnh đó còn có thể sử dụng app để đưa ra các yêu cầu về tài liệu cần tìm kiếm.
App có thể dựa vào dữ liệu thu thập được từ người dùng và dữ liệu từ các website để đưa ra các gợi ý tài liệu phù hợp.
Và một chức năng nhỏ, nếu người dùng yêu cầu xây dựng một lột trình học tập, ví dụ "muốn trở thành một data engineer"
app có thể đề xuất các lộ trình học kèm theo tài liệu liên quan đến lĩnh vực đó.
## 2. Các bước thực hiện

- Bước 1: Người dùng truy cập vào app
- Bước 2: Người dùng chọn chức năng quản lý tài liệu
- Bước 3: Người dùng nhập thông tin tài liệu cần tìm kiếm
- Bước 4: App hiển thị kết quả tìm kiếm

## 3. Kết quả mong đợi

- Nếu app không đưa ra được đề thi như ý của người dùng thì cần xóa sạch dữ liệu và tạo mới, nếu không tạo được
sau một khoảng nhất định thì sẽ hiển thị lỗi 

### Mô tả chi tiết 
tìm kiếm theo nhiều tiêu chí khác nhau như tên tài liệu, tác giả, nội dung, ngày tạo, ngày cập nhật, loại tài liệu, ...

# Quy trình phát triển chức năng

## Tất cả đều phải test kỹ chức khi chuyển qua giai đoạn mới 

#### Giai đoạn 1: tập trung vào việc xây dựng hệ thống cơ bản, đảm bảo tính ổn định và khả năng mở rộng trong tương lai.

- Xây dựng cơ sở dữ liệu
- Xây dựng API upload tài liệu
- Xây dựng hệ thống trích xuất dữ liệu từ tài liệu
- Xây dựng API đồng bộ từ google drive
- Quản lý phiên bản cơ bản

##### Các chức năng cần có 

* Upload tài liệu
  - Mô tả: Người dùng tải tệp (PDF, Word, hình ảnh) lên server, hệ thống lưu tệp và trích xuất dữ liệu.
    - Đầu vào: Tệp từ người dùng.
    - Đầu ra: Tệp được lưu trên server, metadata và nội dung trích xuất được lưu trong cơ sở dữ liệu.
* Trích xuất dữ liệu
  - Mô tả: Trích xuất văn bản và hình ảnh từ tài liệu đã upload hoặc đồng bộ từ Google Drive.
    - Đầu vào: Tệp tài liệu.
    - Đầu ra: Văn bản và hình ảnh được trích xuất.
* Đồng bộ từ Google Drive
  - Mô tả: Người dùng cung cấp liên kết Google Drive, hệ thống đồng bộ tài liệu và các phiên bản của nó.
    - Đầu vào: URL Google Drive (tài liệu hoặc thư mục).
    - Đầu ra: Metadata của tài liệu và phiên bản được lưu trong cơ sở dữ liệu.
* Quản lý phiên bản
    - Mô tả: Liệt kê các phiên bản của tài liệu và cho phép tải xuống phiên bản cụ thể.
        - Đầu vào: ID tài liệu hoặc liên kết Google Drive.
        - Đầu ra: Danh sách các phiên bản và thông tin chi tiết của mỗi phiên bản.
* Xem danh sách tài liệu
    - Mô tả: Hiển thị danh sách tài liệu đã upload hoặc đồng bộ từ Google Drive.
        - Đầu vào: Không.
        - Đầu ra: Danh sách tài liệu với metadata (tên, ngày tạo, kích thước).


#### Giai đoạn 2: giải quyết các vấn đề về hiệu suất, khả năng mở rộng, và quản lý dữ liệu lớn.

- Tích hợp tìm kiếm full-text
- Chuyển sang lưu trữ phân tán
- Xử lý bất đồng bộ
- Tối ưu hóa cơ sở dữ liệu
- Test với big data

##### Các chức năng cần có

* Tìm kiếm full-text
  - Mô tả: Người dùng có thể tìm kiếm tài liệu dựa trên nội dung văn bản bên trong (ví dụ: "tất cả tài liệu chứa từ 'hợp đồng'").
    - Đầu vào: Từ khóa hoặc cụm từ tìm kiếm.
    - Đầu ra: Danh sách tài liệu phù hợp với metadata và đoạn trích nội dung liên quan.
* Lưu trữ phân tán
    - Mô tả: Tài liệu được lưu trên AWS S3 thay vì hệ thống tệp cục bộ, hỗ trợ mở rộng dễ dàng.
        - Đầu vào: Tệp tài liệu từ upload hoặc Google Drive.
        - Đầu ra: URL S3 của tệp được lưu trong cơ sở dữ liệu.
* Xử lý bất đồng bộ
    - Mô tả: Các tác vụ như upload tài liệu, trích xuất dữ liệu, và đồng bộ từ Google Drive được xử lý bất đồng bộ để tăng hiệu suất.
        - Đầu vào: Các yêu cầu từ người dùng hoặc hệ thống.
        - Đầu ra: Thông báo trạng thái (ví dụ: "Đang xử lý", "Hoàn thành").
* Phân trang và lọc danh sách tài liệu
    - Mô tả: Hiển thị danh sách tài liệu theo trang (pagination) và hỗ trợ lọc (theo tên, ngày, kích thước).
        - Đầu vào: Số trang, kích thước trang, tiêu chí lọc.
        - Đầu ra: Danh sách tài liệu phân trang.

#### Giai đoạn 3: 


