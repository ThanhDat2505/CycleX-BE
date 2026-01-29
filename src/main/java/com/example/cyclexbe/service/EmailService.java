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
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("Xác thực tài khoản CycleX");
            helper.setText(buildOtpHtml(otp), true); // true = HTML

            mailSender.send(mimeMessage);
            return true;

        } catch (MessagingException | MailException ex) {
            System.out.printf(
                    "Send OTP mail failed to %s lỗi %s%n",
                    toEmail, ex.getMessage()
            );
            return false;
        }
    }

    private String buildOtpHtml(String otp) {
        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body {
                    margin: 0;
                    padding: 0;
                    background-color: #0f172a;
                    font-family: Arial, sans-serif;
                    color: #ffffff;
                }
                .container {
                    max-width: 600px;
                    margin: 40px auto;
                    background-color: #111827;
                    border-radius: 12px;
                    overflow: hidden;
                    box-shadow: 0 10px 30px rgba(0,0,0,0.4);
                }
                .header {
                    padding: 24px;
                    background: linear-gradient(90deg, #f97316, #fb923c);
                    font-size: 22px;
                    font-weight: bold;
                    text-align: center;
                    color: #ffffff;
                }
                .content {
                    padding: 32px;
                    line-height: 1.6;
                }
                .otp-box {
                    margin: 28px auto;
                    padding: 16px;
                    background-color: #020617;
                    border-radius: 10px;
                    text-align: center;
                }
                .otp {
                    font-size: 36px;
                    font-weight: bold;
                    letter-spacing: 8px;
                    color: #f97316;
                }
                .hint {
                    margin-top: 20px;
                    font-size: 14px;
                    color: #9ca3af;
                }
                .divider {
                    margin: 32px 0;
                    height: 1px;
                    background-color: #1f2933;
                }
                .footer {
                    padding: 20px;
                    text-align: center;
                    font-size: 12px;
                    color: #9ca3af;
                    background-color: #020617;
                }
                .support {
                    color: #f97316;
                    text-decoration: none;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    CycleX – Xác Thực Tài Khoản
                </div>

                <div class="content">
                    <p>Xin chào,</p>

                    <p>
                        Chúng tôi nhận được yêu cầu xác thực tài khoản CycleX của bạn.
                        Để tiếp tục, vui lòng sử dụng mã OTP bên dưới:
                    </p>

                    <div class="otp-box">
                        <div class="otp">%s</div>
                    </div>

                    <p>
                        Mã OTP này có hiệu lực trong <strong>2 phút</strong>.
                        Sau thời gian này, bạn sẽ cần yêu cầu mã mới.
                    </p>

                    <div class="divider"></div>

                    <p class="hint">
                        🔒 Vì lý do bảo mật, vui lòng không chia sẻ mã này cho bất kỳ ai,
                        kể cả nhân viên CycleX.
                    </p>

                    <p class="hint">
                        Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email
                        hoặc liên hệ đội ngũ hỗ trợ của chúng tôi để được trợ giúp.
                    </p>
                </div>

                <div class="footer">
                    © 2026 CycleX – Nền tảng mua bán xe đạp uy tín<br>
                    Hỗ trợ: <a class="support" href="mailto:support@cyclex.vn">support@cyclex.vn</a>
                </div>
            </div>
        </body>
        </html>
        """.formatted(otp);
    }
}
