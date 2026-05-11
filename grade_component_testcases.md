# Kế hoạch kiểm thử: Quản lý thành phần điểm (Grade Components)

## 1. Thông tin chung
- **Chức năng:** Thêm mới thành phần điểm (Add Grade Component)
- **Đối tượng test:** Form nhập liệu thành phần điểm.
- **Môi trường:** Localhost (cần chạy server Spring Boot trước khi test).

## 2. Danh sách Test Cases

| STT | Mã TC | Tên Test Case | Mô tả các bước | Kết quả mong đợi |
| :--- | :--- | :--- | :--- | :--- |
| 1 | TC-01 | Thêm mới thành công | 1. Nhập UUID lớp học phần hợp lệ<br>2. Nhập Mã, Tên, Trọng số hợp lệ<br>3. Nhấn "Thêm mới" | Hệ thống báo thành công, dữ liệu được lưu vào DB. |
| 2 | TC-02 | Bỏ trống trường bắt buộc | 1. Để trống trường "Tên thành phần"<br>2. Nhấn "Thêm mới" | Hệ thống hiển thị thông báo lỗi yêu cầu nhập Tên. |
| 3 | TC-03 | Trọng số không hợp lệ | 1. Nhập Trọng số = 150 (quá 100)<br>2. Nhấn "Thêm mới" | Hệ thống báo lỗi giá trị trọng số phải từ 0-100. |
| 4 | TC-04 | Kiểm tra nút Hủy bỏ | 1. Nhập một số dữ liệu<br>2. Nhấn "Hủy bỏ và quay lại" | Hệ thống chuyển hướng về trang danh sách, không lưu dữ liệu. |
| 5 | TC-05 | Kiểm tra ký tự đặc biệt | 1. Nhập Tên thành phần có dấu tiếng Việt: "Điểm chuyên cần"<br>2. Nhấn "Thêm mới" | Hệ thống lưu đúng định dạng Unicode (không bị lỗi font). |

## 3. Ghi chú
- Cần đảm bảo `course_section_id` tồn tại trong database trước khi chạy test case 1.
- Kiểm tra lại ràng buộc `UNIQUE` cho `component_code` (không được trùng mã trong cùng 1 lớp).
