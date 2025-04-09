# Quy tắc code chung (sửa đổi gần nhất 09/04/2025)
## Quy tắc đặt tên
- Tên class tuân theo PascalCase. Eg: MyClass, Student
- Tên biến bình thường tuân theo camelCase và không đặt biến kiểu vô nghĩa. Eg: studentAge, accountProfile, account
- Tên biến static + final tuân theo SCREAMING_SNAKE_CASE. Eg: SECRET_KEY, PRIVATE_KEY
- Tên bảng, trường dữ liệu, collections trong database tuân theo snake_case. Eg: account_profile
- Tên package code tuân theo lowercase, no space. Eg: common, accountmanager
- RESTful API tuân theo kebab-case và cấu trúc kiểu /api/{version}/{chuc-nang} . Eg:
    + /api/v1/login
    + /api/v1/get-user-profile

## Quy tắc quản lý mật khẩu nguồn dữ liệu
- Không lưu mật khẩu bằng hard-code, application.yml trường hợp test thì xong nhớ xóa
- Lưu mật khẩu trong file .env và không push lên github
- Không đẩy file chứa mật khẩu, pem key lên github (đặt trong .gitignore)

## Quy tắc lưu trữ file
- Package common chứa các service, utils, các configuration, security, config, filter hoặc các lớp dùng chung cả chương trình
- Package model chứa các package như enums, mappers, request, response
- Package entity chứa các lớp dữ liệu trên database
- Package service chứa các interface của service, package impl chứa các implement của các service
- Package repo chứa các repository
- Package specification chứa các specifications (chỉ dùng specifications khi truy vấn phức tạp lắm luôn, nhưng suggest tạo procedure)

## Quy tắc code
- Trong lớp service dùng thoải mái các exception nhưng trong controller dùng các exception như:
    + BadRequestException: request không hợp lệ, sai tham số, vv
    + ResourceNotFoundException: hông có dữ liệu tìm thấy. (Thường dùng cho kiểu url không hợp lệ. Nếu là API tìm dữ liệu search thì nên trả về rỗng thay vì 404)
    + RuntimeException: Lỗi từ hệ thống
    + Chỉ cần throw new {exception} nó sẽ tự response ra lỗi á.
    + Response ok thì không cần thêm gì vô đâu nhá. Ae viết cái Response cho từng response cụ thể
- Nên dùng @Valid cho các body từ request để tự validate và trả về(tôi config sẵn rồi, ko xài tôi buồn ae)
- Mấy phương thức deprecated ae đừng dùng, chịu khó research tài liệu hoặc hỏi ae

## Trường hợp nếu phát hiện lỗi từ code của ae khác mà mình dùng bị ảnh hưởng
- Tạo cái issue trên github rồi nhắn tin báo.
- Có thể mô tả lỗi nó như nào, hoặc nếu biết fix thì mô tả ngắn gọn hoặc kêu là tôi sửa code của bro nha nó lỗi ABC XYZ
- Nếu bro đó bận quá không rep được thì tự tạo mình fix bug

## Cách dùng git/github cơ bản
- Xem trên Tips JavaScript [ở đây ](https://www.youtube.com/watch?v=vQgcl8VouLU)
- Khi code một chức năng nào đó thì đẩy lên branch feature/{tên chức năng} xong tạo pull request merge vô **develop** rồi hú mình hoặc ai đó review code
- Không tạo pull request vô **master** (trừ khi hotfix)
- Message commit nếu có trong issue thì viết kiểu git commit -m '#{number} - {thông điệp gì đó}'

# Sai phạm QUY TẮC trên lần đầu nhắc nhở lần sau + 20.000 VNĐ vào ngân sách
### Author: Nguyễn Quốc Bảo


