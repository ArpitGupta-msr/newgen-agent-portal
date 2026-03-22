export const ERRORS = {
  INVALID_AGENCY_CODE: 'Incorrect agency code, please enter a valid code.',
  INVALID_OTP: 'Incorrect OTP, please try again',
  PASSWORD_CRITERIA: 'Password does not meet the recommended criteria.',
  PASSWORD_MISMATCH: 'Passwords do not match',
  MPIN_MISMATCH: 'MPINs do not match',
  MPIN_FORMAT: 'MPIN must be exactly 4 digits.',
  INCORRECT_PASSWORD: 'Incorrect password. Please try again',
  INCORRECT_MPIN: 'Incorrect MPIN. Please try again.',
  REQUIRED_FIELD: (name) => `Please provide a valid ${name}`,
  OTP_INCOMPLETE: 'Please enter the complete 6-digit OTP',
  OTP_SEND_FAILED: 'Failed to send OTP. Please try again.',
  OTP_RESEND_FAILED: 'Failed to resend OTP',
  SET_PASSWORD_FAILED: 'Failed to set password. Please try again.',
  SET_MPIN_FAILED: 'Failed to set MPIN. Please try again.',
};

export const SUCCESS = {
  CODE_VERIFIED: 'Agency code verified successfully',
  OTP_VERIFIED: (name) => `Hi ${name}. Your verification has been done successfully.`,
  PASSWORD_SET: 'Your password has been set up successfully. You can now login to your account.',
  MPIN_SET: 'Your MPIN has been set up successfully. You can now login to your account.',
  LOGIN: 'You have successfully logged in to your account.',
};
