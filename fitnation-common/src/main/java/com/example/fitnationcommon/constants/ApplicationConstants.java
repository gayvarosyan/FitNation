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

        public static final String LOG_TRAINER_CREATED = "Trainer created: id={}, email={}, status={}";
        public static final String LOG_TRAINER_INVITATION_EMAIL_SENT = "Invitation email sent to: {}";
        public static final String LOG_EMAIL_INVITATION_SEND_FAILED = "Failed to send invitation email to: {}";
        public static final String LOG_EMAIL_INVITATION_TEMPLATE_LOAD_FAILED =
                "Failed to load trainer invitation email template";
        public static final String EMAIL_SUBJECT_TRAINER_INVITATION = "Welcome to FitNation - Your Account Invitation";

        public static final int NAME_MAX_SIZE = 50;
        public static final int PHONE_MAX_LENGTH = 50;

        public static final String FIRST_NAME_REQUIRED = "First name is required";
        public static final String FIRST_NAME_MAX_SIZE = "First name must not exceed " + NAME_MAX_SIZE + " characters";

        public static final String LAST_NAME_REQUIRED = "Last name is required";
        public static final String LAST_NAME_MAX_SIZE = "Last name must not exceed " + NAME_MAX_SIZE + " characters";

        public static final String EMAIL_REQUIRED = "Email is required";
        public static final String EMAIL_VALID = "Email should be valid";

        public static final String PHONE_REQUIRED = "Phone is required";
        public static final String PHONE_INVALID_FORMAT =
                "Phone must be a valid phone number (10-15 digits, optional + prefix).";
        public static final String PHONE_MAX_SIZE = "Phone must not exceed " + PHONE_MAX_LENGTH + " characters";

        public static final String PASSWORD_REQUIRED = "Password is required";

        public static final String NAME_REQUIRED = " is required";
        public static final String NAME_MAX_SIZE_EXCEEDED = " must not exceed " + NAME_MAX_SIZE + " characters";
        public static final String NAME_INVALID_CHARS = " can only contain letters, spaces and hyphens";

        public static final String PASSWORD_INVALID = "Password must contain at least one uppercase letter, one digit and one special character";

        public static final String NUTRITION_PLAN_NOT_FOUND = "Nutrition plan not found: ";

        public static final String INVALID_ROLE = "Invalid role";
        public static final String INVALID_REFRESH_TOKEN = "Invalid refresh token";

        public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
        public static final String USER_PENDING_CANNOT_EDIT = "Cannot edit user while status is PENDING. User must login first.";

        public static final String MEMBER_NOT_FOUND = "Member not found with id: ";

        public static final String USER_BLOCKED = "User is blocked";
        public static final String USER_INACTIVE = "User is inactive";

        public static final String REQUIRED_PARAM_MISSING = "Required request parameter is missing.";

        public static final String BODY_NOT_READABLE = "Request body could not be parsed.";

        public static final String VALIDATION_REQUEST_FAILED = "Request validation failed.";

        public static final String UNEXPECTED_AUTHENTICATION = "Unexpected authentication state.";

        public static final String CLASS_SCHEDULE_FULL = "No seats available for this class.";
        public static final String CLASS_ALREADY_BOOKED = "You already booked this class.";

        public static final String USER_NOT_FOUND = "User not found: ";
        public static final String INVALID_CREDENTIALS = "Invalid credentials";

        public static final String FREEZE_NOT_OWNER = "You can only manage freeze requests for your own membership.";
        public static final String FREEZE_MEMBERSHIP_NOT_FOUND = "Membership not found.";
        public static final String FREEZE_REQUEST_NOT_FOUND = "Freeze request not found.";
        public static final String FREEZE_INVALID_STATUS = "Only ACTIVE memberships can be frozen.";
        public static final String FREEZE_END_BEFORE_START = "freezeEnd must be on or after freezeStart.";
        public static final String FREEZE_MAX_DAYS_EXCEEDED = "Freeze period cannot exceed 90 days.";
        public static final String FREEZE_MIN_NOTICE_DAYS = "Freeze must start at least 1 day from today.";
        public static final String FREEZE_OVERLAP_EXISTS = "A pending or approved freeze request already overlaps the requested period.";
        public static final String FREEZE_ALREADY_REVIEWED = "This freeze request has already been reviewed.";
        public static final String RENEW_NOT_OWNER = "You can only renew your own membership.";
        public static final String RENEW_INVALID_STATUS = "EXPIRED or CANCELLED memberships cannot be renewed. Use re-purchase instead.";
        public static final String MSG_CONVERSATION_NOT_FOUND = "Conversation not found: ";
        public static final String MSG_NOT_CONVERSATION_PARTICIPANT = "You are not a participant of this conversation";
        public static final int CHAT_MESSAGE_MAX_LENGTH = 8000;
        public static final String MESSAGE_BODY_NOT_BLANK = "Message body cannot be blank";
        public static final String MESSAGE_TOO_LONG = "Message too long";
        public static final String INVALID_TOKEN = "Invalid token";
        public static final String QR_INVALID_FORMAT = "Invalid QR payload format";
        public static final String QR_INVALID_ENCODING = "Invalid QR payload encoding";
        public static final String QR_SESSION_NOT_FOUND = "Session not found";
        public static final String QR_ALREADY_USED = "QR code has already been used";
        public static final String QR_REVOKED = "Session has been revoked";
        public static final String QR_EXPIRED = "QR code has expired";
        public static final String QR_INVALID_SECRET = "Invalid QR secret";
        public static final String QR_NOT_YOUR_SESSION = "Not your session";
        public static final String QR_RATE_LIMIT_EXCEEDED = "Too many QR sessions created. Try again later.";


        public static final int MAX_FREEZE_DAYS = 90;
        public static final int MIN_NOTICE_DAYS = 1;

        public static final String MSG_TRAINER_ASSIGNMENT_PENDING_EXISTS = "You already have a pending trainer request.";
        public static final String MSG_TRAINER_ALREADY_ASSIGNED = "This trainer is already your assigned trainer.";
        public static final String MSG_TRAINER_CLIENT_ALREADY_HAS_TRAINER = "Client already has an assigned trainer. They must unassign first.";
        public static final String MSG_TRAINER_ASSIGNMENT_REQUEST_NOT_FOUND = "Trainer assignment request not found: ";
        public static final String MSG_TRAINER_ASSIGNMENT_NOT_OWNER = "This request does not belong to you.";
        public static final String MSG_TRAINER_ASSIGNMENT_NOT_PENDING = "Request is already ";

        public static final String REQUEST_ID_REQUIRED = "requestId is required";
        public static final String TRAINER_ID_REQUIRED = "trainerId is required";
        public static final int MESSAGE_MAX_LENGTH = 1000;
        public static final String MESSAGE_MAX_SIZE = "Message must not exceed 1000 characters";
  
        public static final int QR_SESSION_TTL_MINUTES = 5;
        public static final int QR_MAX_SESSIONS_PER_WINDOW = 5;
        public static final int QR_RATE_LIMIT_WINDOW_MINUTES = 10;

        public static final long DB_OPERATION_INFO_THRESHOLD_MS = 500;
        public static final long DB_OPERATION_WARN_THRESHOLD_MS = 1000;
        public static final int MAX_PAGE_SIZE = 100;

        public static final long SLOW_API_THRESHOLD_MS = 2000;
        public static final long INFO_API_THRESHOLD_MS = 1000;

        public static final String[] HIGH_FREQUENCY_ENDPOINTS = {
                "/conversations",
                "/messages",
                "/bookings",
                "/memberships"
        };

        public static final String START_TIME_ATTRIBUTE = "startTime";
}