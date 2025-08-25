package com.bravos.steak.common.service.helper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Lớp tiện ích để tạo nội dung email hóa đơn mua game
 * Sử dụng template HTML và thay thế các placeholder bằng dữ liệu thực tế
 */
public class InvoiceEmailGenerator {

    private static final String TEMPLATE = """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Hóa đơn mua game - Steak</title>
            </head>
            <body style="margin: 0; padding: 20px; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f5f7fa; color: #333;">
            
            <!-- Main Container -->
            <table style="max-width: 900px; margin: 0 auto; background-color: #ffffff; border-radius: 12px; box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1); overflow: hidden; border-collapse: collapse;">
            
                <!-- Header -->
                <tr>
                    <td style="background: linear-gradient(135deg, #1b2838 0%, #2a475e 100%); padding: 40px 30px; text-align: center;">
                        <h1 style="margin: 0; color: #ffffff; font-size: 32px; font-weight: bold; letter-spacing: 1px;">
                            <img loading="eager" height="28px" src="https://ccdn.steak.io.vn/logo_steak.svg" alt="Logo">
                            Steak
                        </h1>
                        <p style="margin: 8px 0 0 0; color: #66c0f4; font-size: 14px; opacity: 0.9;">Cảm ơn bạn đã mua hàng!</p>
                    </td>
                </tr>
            
                <!-- Invoice Info Section -->
                <tr>
                    <td style="background-color: #f8fafc; padding: 30px; border-bottom: 1px solid #e2e8f0;">
                        <table style="width: 100%; border-collapse: collapse;">
                            <tr>
                                <td style="vertical-align: top; width: 50%;">
                                    <h2 style="margin: 0 0 8px 0; color: #1e293b; font-size: 24px; font-weight: bold;">Hóa đơn
                                        #{INVOICE_NUMBER}</h2>
                                    <p style="margin: 0; color: #64748b; font-size: 14px;">Ngày: {INVOICE_DATE}</p>
                                    <p style="margin: 4px 0 0 0; color: #64748b; font-size: 14px;">Phương thức: {PAYMENT_METHOD}</p>
                                </td>
                                <td style="vertical-align: top; text-align: right;">
                                    <h3 style="margin: 0 0 8px 0; color: #1e293b; font-size: 16px;">Thông tin khách hàng</h3>
                                    <p style="margin: 0; color: #64748b; font-size: 14px; font-weight: 600;">{CUSTOMER_NAME}</p>
                                    <p style="margin: 4px 0 0 0; color: #64748b; font-size: 14px;">{CUSTOMER_EMAIL}</p>
                                    <p style="margin: 4px 0 0 0; color: #64748b; font-size: 14px;">ID: {CUSTOMER_ID}</p>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            
                <!-- Items Section -->
                <tr>
                    <td style="padding: 30px;">
                        <h3 style="margin: 0 0 25px 0; color: #1e293b; font-size: 20px; font-weight: bold; border-bottom: 3px solid #66c0f4; padding-bottom: 10px; display: inline-block;">
                            🛒 Chi tiết đơn hàng</h3>
            
                        {GAME_ITEMS}
            
                    </td>
                </tr>
            
                <!-- Total Section -->
                <tr>
                    <td style="background-color: #f8fafc; padding: 30px; border-top: 1px solid #e2e8f0;">
                        <table style="width: 100%; border-collapse: collapse;">
                            <tr style="border-top: 2px solid #66c0f4;">
                                <td style="text-align: right; padding: 15px 0 8px 0;">
                                    <span style="color: #1e293b; font-size: 20px; font-weight: bold;">Tổng cộng:</span>
                                </td>
                                <td style="text-align: right; padding: 15px 0 8px 0;">
                                    <span style="color: #059669; font-size: 24px; font-weight: bold;">{TOTAL_AMOUNT}</span>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            
                <!-- Download Instructions -->
                <tr>
                    <td style="background: linear-gradient(135deg, #ecfdf5 0%, #f0fdf4 100%); padding: 30px; border-top: 1px solid #e2e8f0;">
                        <h3 style="margin: 0 0 15px 0; color: #059669; font-size: 18px; font-weight: bold;">📥 Hướng dẫn tải
                            game</h3>
                        <ol style="margin: 0; padding-left: 20px; color: #166534; font-size: 14px; line-height: 1.6;">
                            <li style="margin-bottom: 8px;">Đăng nhập vào tài khoản Steak Store của bạn trên Game Launcher</li>
                            <li style="margin-bottom: 8px;">Vào mục "Thư viện game" để xem các game đã mua</li>
                            <li style="margin-bottom: 8px;">Nhấn "Tải xuống" để cài đặt game</li>
                            <li style="margin-bottom: 8px;">Sử dụng GameStore Launcher để quản lý game</li>
                        </ol>
                    </td>
                </tr>
            
                <!-- Footer -->
                <tr>
                    <td style="background-color: #1e293b; padding: 25px; text-align: center; color: #94a3b8;">
                        <p style="margin: 0 0 8px 0; font-size: 13px;">© 2025 Steak. Tất cả quyền được bảo lưu.</p>
                        <p style="margin: 0 0 12px 0; font-size: 12px;">BravosTeam</p>
                        <table style="margin: 0 auto; border-collapse: collapse;">
                            <tr>
                                <td style="padding: 0 10px;">
                                    <a href="https://steak.io.vn/privacy-policy.html" style="color: #66c0f4; text-decoration: none; font-size: 12px;">Chính sách bảo mật</a>
                                </td>
                                <td style="padding: 0 10px;">
                                    <a href="https://steak.io.vn/terms-of-service.html" style="color: #66c0f4; text-decoration: none; font-size: 12px;">Điều khoản sử dụng</a>
                                </td>
                            </tr>
                        </table>
                    </td>
                </tr>
            
            </table>
            
            </body>
            </html>
            """;

    private static final String GAME_ITEM_TEMPLATE = """
            <!-- Game Item -->
            <table style="width: 100%; margin-bottom: 25px; border: 1px solid #e2e8f0; border-radius: 8px; overflow: hidden; border-collapse: collapse;">
                <tr>
                    <td style="padding: 20px; vertical-align: top; width: 100px;">
                        <img src="{GAME_IMAGE}" alt="{GAME_NAME}"
                             style="height: 80px; border-radius: 8px; object-fit: cover; box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);">
                    </td>
                    <td style="padding: 20px; vertical-align: top;">
                        <h4 style="margin-top: 22px; color: #1e293b; font-size: 18px; font-weight: bold;">{GAME_NAME}</h4>
                    </td>
                    <td style="padding: 20px; text-align: right; vertical-align: top; min-width: 120px;">
                        {PRICE_SECTION}
                    </td>
                </tr>
            </table>
            """;

    /**
     * Tạo nội dung email hóa đơn
     *
     * @param invoice Đối tượng hóa đơn chứa thông tin cần thiết
     * @return Nội dung HTML của email
     */
    public static String generateInvoiceEmail(Invoice invoice) {
        String content = TEMPLATE;

        // Thay thế thông tin hóa đơn
        content = content.replace("{INVOICE_NUMBER}", invoice.getInvoiceNumber());

        content = content.replace("{INVOICE_DATE}", formatDate(invoice.getInvoiceDate()));
        content = content.replace("{PAYMENT_METHOD}", invoice.getPaymentMethod());

        // Thay thế thông tin khách hàng
        content = content.replace("{CUSTOMER_NAME}", invoice.getCustomer().getName());
        content = content.replace("{CUSTOMER_EMAIL}", invoice.getCustomer().getEmail());
        content = content.replace("{CUSTOMER_ID}", invoice.getCustomer().getId());

        // Tạo danh sách game
        String gameItems = generateGameItems(invoice.getGameItems());
        content = content.replace("{GAME_ITEMS}", gameItems);

        // Thay thế tổng tiền
        content = content.replace("{TOTAL_AMOUNT}", formatCurrency(invoice.getTotalAmount()));

        return content;
    }

    /**
     * Tạo HTML cho danh sách game items
     */
    private static String generateGameItems(List<GameItem> gameItems) {
        StringBuilder sb = new StringBuilder();

        for (GameItem item : gameItems) {
            String gameItemHtml = GAME_ITEM_TEMPLATE;

            gameItemHtml = gameItemHtml.replace("{GAME_IMAGE}",
                    item.getImageUrl() != null ? item.getImageUrl() : getDefaultGameImage());
            gameItemHtml = gameItemHtml.replace("{GAME_NAME}", item.getName());

            // Tạo section giá
            String priceSection = generatePriceSection(item);
            gameItemHtml = gameItemHtml.replace("{PRICE_SECTION}", priceSection);

            sb.append(gameItemHtml);
        }

        return sb.toString();
    }

    /**
     * Tạo HTML cho phần giá (có thể có giảm giá)
     */
    private static String generatePriceSection(GameItem item) {
        if (item.hasDiscount()) {
            return String.format("""
                            <p style="margin: 0 0 4px 0; color: #94a3b8; font-size: 14px; text-decoration: line-through;">%s</p>
                            <p style="margin: 0; color: #059669; font-size: 20px; font-weight: bold;">%s</p>
                            <span style="background-color: #dc2626; color: white; padding: 2px 6px; border-radius: 4px; font-size: 10px; font-weight: bold; margin-top: 4px; display: inline-block;">-%d%%</span>
                            """,
                    formatCurrency(item.getOriginalPrice()),
                    formatCurrency(item.getDiscountedPrice()),
                    item.getDiscountPercentage());
        } else {
            return String.format("""
                    <p style="margin: 0; color: #059669; font-size: 20px; font-weight: bold;">%s</p>
                    """, formatCurrency(item.getPrice()));
        }
    }

    /**
     * Format ngày tháng theo định dạng Việt Nam
     */
    private static String formatDate(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm", Locale.of("vi", "VN"));
        return dateTime.format(formatter);
    }

    /**
     * Format tiền tệ theo định dạng Việt Nam
     */
    private static String formatCurrency(double amount) {
        return String.format("%,.0f ₫", amount);
    }

    /**
     * Lấy URL ảnh mặc định cho game
     */
    private static String getDefaultGameImage() {
        return "https://via.placeholder.com/80x80/1b2838/66c0f4?text=GAME";
    }

    /**
     * Tạo email với template tùy chỉnh
     *
     * @param templateData   Map chứa các cặp key-value để thay thế trong template
     * @param customTemplate Template tùy chỉnh (optional)
     * @return Nội dung HTML
     */
    public static String generateCustomEmail(Map<String, String> templateData, String customTemplate) {
        String template = customTemplate != null ? customTemplate : TEMPLATE;

        for (Map.Entry<String, String> entry : templateData.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }

        return template;
    }
}
