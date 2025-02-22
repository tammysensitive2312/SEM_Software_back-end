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
