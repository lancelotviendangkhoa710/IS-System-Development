package com.bakery.presenters;

import com.bakery.views.interfaces.IBaseView;
import javafx.application.Platform;

/**
 * Lớp cơ sở cho các Presenter trong hệ thống.
 * Cung cấp các phương thức tiện ích để xử lý luồng (Threading) và giao tiếp với View.
 * 
 * @param <V> Kiểu của View Interface (phải kế thừa IBaseView)
 */
public abstract class BasePresenter<V extends IBaseView> {
    
    protected final V view;

    public BasePresenter(V view) {
        this.view = view;
    }

    /**
     * Thực thi một tác vụ bất đồng bộ (Background Thread) và cập nhật kết quả lên UI.
     * Tự động quản lý trạng thái Loading của View.
     * 
     * @param <T> Kiểu dữ liệu trả về của tác vụ
     * @param backgroundTask Tác vụ chạy ngầm (truy xuất DB/Service)
     * @param onUiSuccess Hành động thực hiện trên UI khi thành công
     */
    protected <T> void runTask(BackgroundTask<T> backgroundTask, UiResultAction<T> onUiSuccess) {
        view.setLoading(true);
        view.xoaLoi();

        Thread thread = new Thread(() -> {
            try {
                T result = backgroundTask.execute();
                Platform.runLater(() -> {
                    view.setLoading(false);
                    onUiSuccess.onResult(result);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    view.setLoading(false);
                    view.hienThiLoi(e.getMessage() != null ? e.getMessage() : "Lỗi không xác định");
                    System.err.println("[BasePresenter] Task Error: " + e.getMessage());
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Thực thi một tác vụ bất đồng bộ không trả về kết quả (Void).
     */
    protected void runTask(SimpleBackgroundTask backgroundTask, SimpleUiAction onUiSuccess) {
        view.setLoading(true);
        view.xoaLoi();

        Thread thread = new Thread(() -> {
            try {
                backgroundTask.execute();
                Platform.runLater(() -> {
                    view.setLoading(false);
                    onUiSuccess.onComplete();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    view.setLoading(false);
                    view.hienThiLoi(e.getMessage() != null ? e.getMessage() : "Lỗi không xác định");
                    System.err.println("[BasePresenter] SimpleTask Error: " + e.getMessage());
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @FunctionalInterface
    public interface BackgroundTask<T> {
        T execute() throws Exception;
    }

    @FunctionalInterface
    public interface UiResultAction<T> {
        void onResult(T result);
    }

    @FunctionalInterface
    public interface SimpleBackgroundTask {
        void execute() throws Exception;
    }

    @FunctionalInterface
    public interface SimpleUiAction {
        void onComplete();
    }
}
