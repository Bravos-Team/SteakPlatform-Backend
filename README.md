# Steak – A Digital Game Marketplace

## Hướng dẫn sử dụng cho team frontend, tester
- Lấy file prod.env, private.pem, public.pem bỏ vào thư mục dự án
- Mở cửa sổ terminal sau đó chạy: `docker compose up -d`
- Để reset lại từ đầu hoặc có thay đổi về code `docker compose up -d --build && docker image prune -a`

## Hướng dẫn sử dụng cho team dev
- Bỏ file .env, private.pem, public.pem vào dự án
- Mở cửa sổ terminal sau đó chạy: `docker compose -f docker-compose-dev.yml up -d`
- Rồi chạy code bình thường
- Nếu có lỗi không chạy được do bị chiếm port thì `docker stop steak-server` rồi chạy lại

## Xoá hết container reset lại (chỉ dùng khi lỗi quá nhiều)
- `docker compose down -v && docker compose -f docker-compose-dev.yml down -v`