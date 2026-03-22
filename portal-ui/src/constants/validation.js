export const PASSWORD_RULES = [
  { label: 'At least 8 characters', test: (p) => p.length >= 8 },
  { label: 'One uppercase letter (A-Z)', test: (p) => /[A-Z]/.test(p) },
  { label: 'One lowercase letter (a-z)', test: (p) => /[a-z]/.test(p) },
  { label: 'One number (0-9)', test: (p) => /\d/.test(p) },
  { label: 'One symbol (@, #, $, etc.)', test: (p) => /[@#$%^&+=!]/.test(p) },
  { label: 'No spaces', test: (p) => !/\s/.test(p) },
];

export const OTP_LENGTH = 6;
export const MPIN_LENGTH = 4;
export const MAX_OTP_RESENDS = 2;
export const OTP_RESEND_INTERVAL_SECONDS = 300;
