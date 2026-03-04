package com.example.fitnationcommon.constants;


public final class ApplicationConstants {

        private ApplicationConstants() {}

        public static final String VALID_EMAIL_MESSAGE = "Email should be valid";
        public static final String VALID_PASSWORD_MESSAGE =
                "Password must be at least 8 characters long, contain at least one uppercase letter, one number, and one special character";

        public static final int SMALL_TEXT = 50;
        public static final int LARGE_TEXT = 250;

        public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";
}