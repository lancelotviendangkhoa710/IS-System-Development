package com.bakery.services;

import com.bakery.model.dao.SessionDAO;
import com.bakery.model.dto.SessionDTO;
import com.bakery.utils.SessionContext;
import com.bakery.utils.UserSession;
import javafx.application.Platform;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.util.Duration;

public class SessionWatchdogService extends ScheduledService<Boolean> {

    /** Khoảng thời gian check (giây). Đủ nhạy mà không quá tải DB. */
    private static final int CHECK_INTERVAL_SECONDS = 30;

    private final SessionDAO sessionDAO = new SessionDAO();
    private Runnable onSessionInvalid;

    public SessionWatchdogService() {
        // Bắt đầu check ngay sau khi start, sau đó cứ mỗi 30s
        setDelay(Duration.seconds(CHECK_INTERVAL_SECONDS));
        setPeriod(Duration.seconds(CHECK_INTERVAL_SECONDS));
        setRestartOnFailure(true); // Tự khởi động lại nếu DB tạm thời không kết nối được
    }

    /**
     * Đăng ký callback sẽ được gọi khi token không còn hợp lệ.
     * Callback chạy trên FX Application Thread — an toàn để cập nhật UI.
     *
     * @param callback Hàm xử lý (vd: chuyển về màn hình đăng nhập)
     */
    public void setOnSessionInvalid(Runnable callback) {
        this.onSessionInvalid = callback;
    }

    @Override
    protected Task<Boolean> createTask() {
        return new Task<>() {
            @Override
            protected Boolean call() throws Exception {
                String token = UserSession.getCurrentToken();
                // Nếu không có token trong memory → session chưa được tạo đúng cách
                if (token == null) {
                    return false;
                }

                SessionDTO session = sessionDAO.findSessionByToken(token);

                // Token không tồn tại trong DB (bị xóa) hoặc bị revoke → phiên không hợp lệ
                if (session == null || !"ACTIVE".equalsIgnoreCase(session.getStatus())) {
                    return false;
                }

                // Kiểm tra hết hạn
                if (session.getExpiresAt() != null &&
                        session.getExpiresAt().before(new java.sql.Timestamp(System.currentTimeMillis()))) {
                    return false;
                }

                return true;
            }
        };
    }

    @Override
    protected void succeeded() {
        super.succeeded();
        Boolean tokenHopLe = getValue();
        if (Boolean.FALSE.equals(tokenHopLe)) {
            // Token không còn hợp lệ → dọn session + kích hoạt callback trên FX thread
            Platform.runLater(() -> {
                SessionContext.clear();
                UserSession.clear();
                if (onSessionInvalid != null) {
                    onSessionInvalid.run();
                }
            });
            cancel(); // Dừng watchdog sau khi đã kick user
        }
    }
}
