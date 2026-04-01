package com.example.cyclexbe.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Xác thực tài khoản CycleX");
            helper.setText(buildOtpHtml(otp), true); // true = HTML

            mailSender.send(mimeMessage);
            return true;

        } catch (MessagingException | MailException ex) {
            System.out.printf(
                    "Send OTP mail failed to %s lỗi %s%n",
                    toEmail, ex.getMessage());
            return false;
        }
    }

    public boolean sendPasswordResetEmail(String toEmail, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Đặt lại mật khẩu CycleX");
            helper.setText(buildPasswordResetHtml(otp), true);

            mailSender.send(mimeMessage);
            return true;

        } catch (MessagingException | MailException ex) {
            System.out.printf(
                    "Send password reset mail failed to %s lỗi %s%n",
                    toEmail, ex.getMessage());
            return false;
        }
    }

    private String buildPasswordResetHtml(String otp) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Đặt lại mật khẩu – CycleX</title>
                </head>
                <body style="margin:0;padding:0;background-color:#0f172a;font-family:'Segoe UI',Arial,sans-serif;color:#e2e8f0;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#0f172a;padding:40px 16px;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%%;">

                                    <!-- LOGO BAR -->
                                    <tr>
                                        <td align="center" style="padding-bottom:24px;">
                                            <table cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="background:linear-gradient(135deg,#ea580c,#f97316);border-radius:12px;padding:10px 22px;">
                                                        <span style="font-size:20px;font-weight:700;color:#ffffff;letter-spacing:1px;">&#x1F6B2; CycleX</span>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- CARD -->
                                    <tr>
                                        <td style="background-color:#1e293b;border-radius:16px;overflow:hidden;box-shadow:0 20px 60px rgba(0,0,0,0.5);">

                                            <!-- HEADER BANNER -->
                                            <table width="100%%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="background:linear-gradient(135deg,#ea580c 0%%,#f97316 50%%,#fb923c 100%%);padding:36px 40px;text-align:center;">
                                                        <div style="font-size:32px;margin-bottom:8px;">&#x1F510;</div>
                                                        <div style="font-size:22px;font-weight:700;color:#ffffff;letter-spacing:0.5px;">Đặt Lại Mật Khẩu</div>
                                                        <div style="font-size:13px;color:#fed7aa;margin-top:4px;">Yêu cầu đặt lại mật khẩu của bạn</div>
                                                    </td>
                                                </tr>
                                            </table>

                                            <!-- BODY -->
                                            <table width="100%%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="padding:40px;">
                                                        <p style="margin:0 0 16px;font-size:15px;color:#cbd5e1;">Xin chào,</p>
                                                        <p style="margin:0 0 28px;font-size:15px;color:#94a3b8;line-height:1.7;">
                                                            Chúng tôi nhận được yêu cầu <strong style="color:#f97316;">đặt lại mật khẩu</strong> cho tài khoản CycleX của bạn.
                                                            Vui lòng sử dụng mã OTP bên dưới để tiếp tục:
                                                        </p>

                                                        <!-- OTP BOX -->
                                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                                            <tr>
                                                                <td align="center" style="padding:8px 0 32px;">
                                                                    <table cellpadding="0" cellspacing="0">
                                                                        <tr>
                                                                            <td style="background:linear-gradient(135deg,#0f172a,#1e293b);border:2px solid #f97316;border-radius:14px;padding:24px 48px;text-align:center;">
                                                                                <div style="font-size:11px;font-weight:600;letter-spacing:3px;color:#94a3b8;text-transform:uppercase;margin-bottom:12px;">Mã xác nhận</div>
                                                                                <div style="font-size:42px;font-weight:700;letter-spacing:12px;color:#f97316;font-family:'Courier New',monospace;">%s</div>
                                                                                <div style="margin-top:14px;display:inline-block;background-color:#1e3a2a;border-radius:20px;padding:5px 14px;">
                                                                                    <span style="font-size:12px;color:#4ade80;font-weight:600;">&#x23F1; Hiệu lực: 5 phút</span>
                                                                                </div>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>

                                                        <!-- DIVIDER -->
                                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                                            <tr>
                                                                <td style="border-top:1px solid #334155;padding-bottom:24px;"></td>
                                                            </tr>
                                                        </table>

                                                        <!-- WARNINGS -->
                                                        <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:12px;">
                                                            <tr>
                                                                <td style="background-color:#1c1f2e;border-left:3px solid #f97316;border-radius:0 8px 8px 0;padding:14px 16px;">
                                                                    <span style="font-size:13px;color:#94a3b8;">
                                                                        &#x1F512; <strong style="color:#e2e8f0;">Bảo mật:</strong> Không chia sẻ mã này cho bất kỳ ai, kể cả nhân viên CycleX.
                                                                    </span>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                                            <tr>
                                                                <td style="background-color:#1c1f2e;border-left:3px solid #475569;border-radius:0 8px 8px 0;padding:14px 16px;">
                                                                    <span style="font-size:13px;color:#64748b;">
                                                                        &#x2139;&#xFE0F; Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email. Mật khẩu của bạn sẽ <strong style="color:#94a3b8;">không bị thay đổi</strong>.
                                                                    </span>
                                                                </td>
                                                            </tr>
                                                        </table>

                                                    </td>
                                                </tr>
                                            </table>

                                        </td>
                                    </tr>

                                    <!-- FOOTER -->
                                    <tr>
                                        <td align="center" style="padding:28px 0 0;">
                                            <p style="margin:0 0 6px;font-size:12px;color:#475569;">© 2026 CycleX – Nền tảng mua bán xe đạp uy tín</p>
                                            <p style="margin:0;font-size:12px;color:#475569;">
                                                Hỗ trợ: <a href="mailto:support@cyclex.vn" style="color:#f97316;text-decoration:none;">support@cyclex.vn</a>
                                            </p>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(otp);
    }

    private String buildOtpHtml(String otp) {
        return """
                <!DOCTYPE html>
                <html lang="vi">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>Xác thực tài khoản – CycleX</title>
                </head>
                <body style="margin:0;padding:0;background-color:#0f172a;font-family:'Segoe UI',Arial,sans-serif;color:#e2e8f0;">
                    <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#0f172a;padding:40px 16px;">
                        <tr>
                            <td align="center">
                                <table width="600" cellpadding="0" cellspacing="0" style="max-width:600px;width:100%%;">

                                    <!-- LOGO BAR -->
                                    <tr>
                                        <td align="center" style="padding-bottom:24px;">
                                            <table cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="background:linear-gradient(135deg,#ea580c,#f97316);border-radius:12px;padding:10px 22px;">
                                                        <span style="font-size:20px;font-weight:700;color:#ffffff;letter-spacing:1px;">&#x1F6B2; CycleX</span>
                                                    </td>
                                                </tr>
                                            </table>
                                        </td>
                                    </tr>

                                    <!-- CARD -->
                                    <tr>
                                        <td style="background-color:#1e293b;border-radius:16px;overflow:hidden;box-shadow:0 20px 60px rgba(0,0,0,0.5);">

                                            <!-- HEADER BANNER -->
                                            <table width="100%%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="background:linear-gradient(135deg,#ea580c 0%%,#f97316 50%%,#fb923c 100%%);padding:36px 40px;text-align:center;">
                                                        <div style="font-size:32px;margin-bottom:8px;">&#x2709;&#xFE0F;</div>
                                                        <div style="font-size:22px;font-weight:700;color:#ffffff;letter-spacing:0.5px;">Xác Thực Tài Khoản</div>
                                                        <div style="font-size:13px;color:#fed7aa;margin-top:4px;">Hoàn tất đăng ký tài khoản CycleX của bạn</div>
                                                    </td>
                                                </tr>
                                            </table>

                                            <!-- BODY -->
                                            <table width="100%%" cellpadding="0" cellspacing="0">
                                                <tr>
                                                    <td style="padding:40px;">
                                                        <p style="margin:0 0 16px;font-size:15px;color:#cbd5e1;">Xin chào,</p>
                                                        <p style="margin:0 0 28px;font-size:15px;color:#94a3b8;line-height:1.7;">
                                                            Chúng tôi nhận được yêu cầu <strong style="color:#f97316;">xác thực tài khoản</strong> CycleX của bạn.
                                                            Vui lòng sử dụng mã OTP bên dưới để tiếp tục:
                                                        </p>

                                                        <!-- OTP BOX -->
                                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                                            <tr>
                                                                <td align="center" style="padding:8px 0 32px;">
                                                                    <table cellpadding="0" cellspacing="0">
                                                                        <tr>
                                                                            <td style="background:linear-gradient(135deg,#0f172a,#1e293b);border:2px solid #f97316;border-radius:14px;padding:24px 48px;text-align:center;">
                                                                                <div style="font-size:11px;font-weight:600;letter-spacing:3px;color:#94a3b8;text-transform:uppercase;margin-bottom:12px;">Mã xác nhận</div>
                                                                                <div style="font-size:42px;font-weight:700;letter-spacing:12px;color:#f97316;font-family:'Courier New',monospace;">%s</div>
                                                                                <div style="margin-top:14px;display:inline-block;background-color:#1e3a2a;border-radius:20px;padding:5px 14px;">
                                                                                    <span style="font-size:12px;color:#4ade80;font-weight:600;">&#x23F1; Hiệu lực: 2 phút</span>
                                                                                </div>
                                                                            </td>
                                                                        </tr>
                                                                    </table>
                                                                </td>
                                                            </tr>
                                                        </table>

                                                        <!-- DIVIDER -->
                                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                                            <tr>
                                                                <td style="border-top:1px solid #334155;padding-bottom:24px;"></td>
                                                            </tr>
                                                        </table>

                                                        <!-- WARNINGS -->
                                                        <table width="100%%" cellpadding="0" cellspacing="0" style="margin-bottom:12px;">
                                                            <tr>
                                                                <td style="background-color:#1c1f2e;border-left:3px solid #f97316;border-radius:0 8px 8px 0;padding:14px 16px;">
                                                                    <span style="font-size:13px;color:#94a3b8;">
                                                                        &#x1F512; <strong style="color:#e2e8f0;">Bảo mật:</strong> Không chia sẻ mã này cho bất kỳ ai, kể cả nhân viên CycleX.
                                                                    </span>
                                                                </td>
                                                            </tr>
                                                        </table>
                                                        <table width="100%%" cellpadding="0" cellspacing="0">
                                                            <tr>
                                                                <td style="background-color:#1c1f2e;border-left:3px solid #475569;border-radius:0 8px 8px 0;padding:14px 16px;">
                                                                    <span style="font-size:13px;color:#64748b;">
                                                                        &#x2139;&#xFE0F; Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email hoặc liên hệ đội ngũ hỗ trợ để được giúp đỡ.
                                                                    </span>
                                                                </td>
                                                            </tr>
                                                        </table>

                                                    </td>
                                                </tr>
                                            </table>

                                        </td>
                                    </tr>

                                    <!-- FOOTER -->
                                    <tr>
                                        <td align="center" style="padding:28px 0 0;">
                                            <p style="margin:0 0 6px;font-size:12px;color:#475569;">© 2026 CycleX – Nền tảng mua bán xe đạp uy tín</p>
                                            <p style="margin:0;font-size:12px;color:#475569;">
                                                Hỗ trợ: <a href="mailto:support@cyclex.vn" style="color:#f97316;text-decoration:none;">support@cyclex.vn</a>
                                            </p>
                                        </td>
                                    </tr>

                                </table>
                            </td>
                        </tr>
                    </table>
                </body>
                </html>
                """
                .formatted(otp);
    }
}
