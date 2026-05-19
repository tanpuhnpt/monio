# Hướng dẫn chạy và sử dụng các tính năng (TrackingApp / FinTrack)

Tài liệu này cung cấp hướng dẫn ngắn gọn để cài đặt môi trường, khởi chạy ứng dụng và kiểm thử các tính năng chính của hệ thống quản lý chi tiêu.

## 1. Cài đặt và khởi chạy (Local Development)

Yêu cầu hệ thống: Máy tính đã cài đặt **Node.js** (khuyến nghị bản LTS 18+ hoặc 20+).

Mở terminal, di chuyển vào thư mục `UI/TrackingApp` và thực hiện các lệnh sau:

```bash
# 1. Cài đặt các gói thư viện phụ thuộc
npm install

# 2. Khởi chạy ứng dụng ở chế độ phát triển
npm run dev
```

Sau khi chạy lệnh trên, ứng dụng sẽ khởi chạy. Bạn truy cập vào đường dẫn được hiển thị trên terminal (thường là `http://localhost:5173`) trên trình duyệt để bắt đầu sử dụng.

---

## 2. Hướng dẫn trải nghiệm các tính năng chính

### 2.1. Đăng ký & Đăng nhập
- **Đăng ký (Register):** Tạo mới một tài khoản với email/mật khẩu.
- **Đăng nhập (Login):** Đăng nhập vào hệ thống.
- **Onboarding:** Nếu là lần đầu tiên sử dụng, ứng dụng sẽ điều hướng đến màn hình chào mừng (Onboarding) để giúp bạn thiết lập "Ví tiền" đầu tiên và điền số dư ban đầu.

### 2.2. Bảng điều khiển (Dashboard)
- Ở trang chủ, bạn sẽ có cái nhìn tổng quan toàn diện về tình trạng tài chính hiện tại bao gồm: Tổng số dư (Balance), tổng thu/chi trong kỳ, và biểu đồ mini.
- Liệt kê trực quan các tính năng và các giao dịch diễn ra gần đây nhất (Recent Transactions).

### 2.3. Quản lý Giao dịch (Transactions)
Truy cập vào trang **Transactions**, bạn có **3 cách** hiện đại để thêm một giao dịch mới:
1. **Nhập thủ công (Manual Form):** Điền các thông tin truyền thống như Số tiền, Danh mục (Ăn uống, Lương...), Ngày giờ và Ghi chú.
2. **Quét Hóa đơn máy học (OCR Scanner):** Sử dụng nút quét hóa đơn (`InAppScanner`). Đưa hình ảnh hóa đơn vào, hệ thống sẽ tự động bóc tách số tiền và nội dung để gợi ý nhập nhanh.
3. **Trợ lý Thông minh thông qua Text/Voice (Voice Chat & Chatbot):** Nhấn vào biểu tượng micro hoặc khung chat, nhập/đọc câu lệnh bằng ngôn ngữ tự nhiên (VD: *"Tôi vừa uống cafe hết 50 ngàn"*). AI sẽ tự động phân tích ý định, bóc tách số tiền, phân loại danh mục và lưu lại giao dịch tương ứng.

### 2.4. Báo cáo & Thống kê (Reports)
- Truy cập tính năng **Reports** để xem các biểu đồ doanh thu và chi tiêu chuyên sâu.
- Theo dõi tỷ lệ chi tiêu cho từng danh mục qua biểu đồ tròn, quan sát diễn biến dòng tiền thu/chi theo tuần/tháng để dễ dàng lập kế hoạch tài chính.

### 2.5. Quản lý Ví (Wallet Manager)
- Tính năng này (thường xem trên thanh điều hướng hoặc Settings) giúp bạn tạo nhiều nguồn quỹ khác nhau: *Ví tiền mặt, Thẻ tín dụng, Thẻ ATM...*
- Có thể thiết lập riêng biệt, xem báo cáo số dư độc lập hoặc gộp chung lại để tính tổng tài sản.

---
**💡 Lưu ý:** Một số tính năng gọi tới chức năng Nâng cao (ví dụ nhận dạng giọng nói, chatbot AI, nhận diện OCR) có thể yêu cầu bạn phải khởi chạy kèm Backend Service thích hợp hoặc có sẵn cấu hình API tại file môi trường(`.env`) của UI để hoạt động được hoàn hảo.
