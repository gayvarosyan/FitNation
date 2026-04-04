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

        public static final int NAME_MAX_SIZE = 50;
        public static final int PHONE_MAX_LENGTH = 50;

        public static final String FIRST_NAME_REQUIRED = "First name is required";
        public static final String FIRST_NAME_MAX_SIZE = "First name must not exceed " + NAME_MAX_SIZE + " characters";

        public static final String LAST_NAME_REQUIRED = "Last name is required";
        public static final String LAST_NAME_MAX_SIZE = "Last name must not exceed " + NAME_MAX_SIZE + " characters";

        public static final String EMAIL_REQUIRED = "Email is required";
        public static final String EMAIL_VALID = "Email should be valid";

        public static final String PHONE_REQUIRED = "Phone is required";
        public static final String PHONE_MAX_SIZE = "Phone must not exceed " + PHONE_MAX_LENGTH + " characters";

        public static final String PASSWORD_REQUIRED = "Password is required";

        public static final String NAME_REQUIRED = " is required";
        public static final String NAME_MAX_SIZE_EXCEEDED = " must not exceed " + NAME_MAX_SIZE + " characters";
        public static final String NAME_INVALID_CHARS = " can only contain letters, spaces and hyphens";

        public static final String PASSWORD_INVALID = "Password must contain at least one uppercase letter, one digit and one special character";
}