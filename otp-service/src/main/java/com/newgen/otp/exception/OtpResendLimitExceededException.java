package com.newgen.otp.exception;

public class OtpResendLimitExceededException extends RuntimeException {

    public OtpResendLimitExceededException(String message) {
        super(message);
    }
}
