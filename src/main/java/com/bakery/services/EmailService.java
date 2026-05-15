package com.bakery.services;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Dịch vụ gửi email qua SMTP (Gmail).
 * Cấu hình đọc từ email.properties trong classpath.
 * Không phụ thuộc vào JavaFX — gọi từ Task/Thread phụ là an toàn.
 */
public class EmailService {

    private final Properties smtpProps = new Properties();
    private final String senderEmail;
    private final String senderPassword;
    private final String senderName;
    private final String otpSubject;
    private final int otpExpireMinutes;

    private static EmailService instance;

    private EmailService() {
        try (InputStream in = getClass().getClassLoader()
                .getResourceAsStream("email.properties")) {
            if (in == null) {
                throw new RuntimeException("Không tìm thấy email.properties trong classpath.");
            }
            Properties cfg = new Properties();
            cfg.load(in);

            smtpProps.put("mail.smtp.host", cfg.getProperty("mail.smtp.host"));
            smtpProps.put("mail.smtp.port", cfg.getProperty("mail.smtp.port"));
            smtpProps.put("mail.smtp.auth", cfg.getProperty("mail.smtp.auth"));
            smtpProps.put("mail.smtp.starttls.enable", cfg.getProperty("mail.smtp.starttls.enable"));
            smtpProps.put("mail.smtp.connectiontimeout", cfg.getProperty("mail.smtp.connectiontimeout", "5000"));
            smtpProps.put("mail.smtp.timeout", cfg.getProperty("mail.smtp.timeout", "5000"));

            senderEmail = cfg.getProperty("mail.sender.email");
            senderPassword = cfg.getProperty("mail.sender.password");
            senderName = cfg.getProperty("mail.sender.name", "H3K BAKERY SYSTEM");
            otpSubject = cfg.getProperty("mail.otp.subject", "Verify OTP code to reset password");
            otpExpireMinutes = Integer.parseInt(cfg.getProperty("mail.otp.expire.minutes", "10"));

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc email.properties: " + e.getMessage(), e);
        }
    }

    /** Singleton thread-safe dùng initialization-on-demand. */
    public static EmailService getInstance() {
        if (instance == null) {
            synchronized (EmailService.class) {
                if (instance == null)
                    instance = new EmailService();
            }
        }
        return instance;
    }

    /**
     * Gửi email OTP đặt lại mật khẩu.
     * 
     * @param toEmail Địa chỉ email người nhận
     * @param otpCode Mã OTP 6 số
     * @throws MessagingException nếu gửi thất bại
     */
    public void guiEmailOtp(String toEmail, String otpCode) throws MessagingException {
        Session session = Session.getInstance(smtpProps, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            InternetAddress from = new InternetAddress(senderEmail);
            from.setPersonal(senderName, "UTF-8");
            message.setFrom(from);
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(otpSubject, "UTF-8");
            message.setContent(xayDungNoiDungHtml(otpCode), "text/html; charset=UTF-8");
            Transport.send(message);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new MessagingException("Lỗi encoding tên người gửi: " + e.getMessage(), e);
        }
    }

    private String xayDungNoiDungHtml(String otpCode) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 32px; background: #FDF8F2; border-radius: 12px; border: 1px solid #E8D5B0;">
                    <div style="text-align: center; margin-bottom: 24px;">
                        <h1 style="color: #D85A30; font-size: 28px; margin: 0;">H3K</h1>
                        <p style="color: #8B7355; font-size: 13px; margin: 4px 0 0;">La Boulangerie H3K</p>
                    </div>
                    <h2 style="color: #2C2C2A; font-size: 18px; margin-bottom: 8px;">Đặt lại mật khẩu</h2>
                    <p style="color: #5C5C5A; font-size: 14px; line-height: 1.6;">
                        Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.
                        Sử dụng mã xác nhận bên dưới:
                    </p>
                    <div style="background: #FFFFFF; border: 2px solid #D85A30; border-radius: 8px; padding: 20px; text-align: center; margin: 24px 0;">
                        <span style="font-size: 36px; font-weight: bold; letter-spacing: 12px; color: #D85A30;">%s</span>
                    </div>
                    <p style="color: #8B7355; font-size: 13px;">
                        ⏳ Mã có hiệu lực trong <strong>%d phút</strong>. Không chia sẻ mã này với bất kỳ ai.
                    </p>
                    <hr style="border: none; border-top: 1px solid #E8D5B0; margin: 24px 0;">
                    <p style="color: #ABABAB; font-size: 11px; text-align: center;">
                        Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.
                    </p>
                </div>
                """
                .formatted(otpCode, otpExpireMinutes);
    }

    public int getOtpExpireMinutes() {
        return otpExpireMinutes;
    }
}
