package com.big.shamba.ui.viewmodels;

public class AuthStatus {
    private final boolean success;
    private final String message;

    public AuthStatus(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
