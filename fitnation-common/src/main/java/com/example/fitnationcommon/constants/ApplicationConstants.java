package com.example.fitnationcommon.constants;


public final class ApplicationConstants {

        private ApplicationConstants() {}

        public static final String VALID_EMAIL_MESSAGE = "Email should be valid";
        public static final String VALID_PASSWORD_MESSAGE =
                "Password must be at least 8 characters long, contain at least one uppercase letter, one number, and one special character";

        public static final String NAME_IS_REQUIRED = "Name is required";
        public static final String TRAINER_IS_REQUIRED = "Trainer is required";
        public static final String CAPACITY_IS_REQUIRED = "Capacity is required";
        public static final String MSG_SCHEDULE_NOT_FOUND = "Schedule not found: ";
        public static final String MSG_USER_NOT_FOUND = "User not found: ";
        public static final String MSG_BOOKING_NOT_FOUND = "Booking not found: ";
        public static final int SMALL_TEXT = 50;
        public static final int LARGE_TEXT = 250;
        public static final String MSG_TRAINER_NOT_FOUND = "Trainer not found: ";
        public static final String MSG_CLASS_NOT_FOUND = "Class not found: ";
        public static final String MSG_SCHEDULE_NOT_FOUND_AFTER_SAVE = "Schedule not found after save: ";
        public static final String MSG_SCHEDULE_NOT_FOUND_AFTER_UPDATE = "Schedule not found after update: ";

        public static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        public static final String PASSWORD_REGEX = "^(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&._\\-#+])[A-Za-z\\d@$!%*?&._\\-#+]{8,}$";

}