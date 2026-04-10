package com.bakery.main;

public final class UserSession {
    private static Integer employeeId;
    private static String employeeName;
    private static String username;
    private static boolean darkModeEnabled;

    private UserSession() {
    }

    public static void login(int newEmployeeId, String newEmployeeName, String newUsername) {
        employeeId = newEmployeeId;
        employeeName = newEmployeeName;
        username = newUsername;
    }

    public static void clear() {
        employeeId = null;
        employeeName = null;
        username = null;
    }

    public static Integer getEmployeeId() {
        return employeeId;
    }

    public static String getEmployeeName() {
        return employeeName;
    }

    public static String getUsername() {
        return username;
    }

    public static boolean isDarkModeEnabled() {
        return darkModeEnabled;
    }

    public static void setDarkModeEnabled(boolean enabled) {
        darkModeEnabled = enabled;
    }
}
