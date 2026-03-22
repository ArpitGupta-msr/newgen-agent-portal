import axios from 'axios';

const agentApi = axios.create({
  baseURL: '/newgen/agents',
  headers: { 'Content-Type': 'application/json' },
});

const otpApi = axios.create({
  baseURL: '/newgen/otp',
  headers: { 'Content-Type': 'application/json' },
});

const loginApi = axios.create({
  baseURL: '/newgen/login',
  headers: { 'Content-Type': 'application/json' },
});

export const agentService = {
  signUp: (data) => agentApi.post('/signup', data),
  validateAgencyCode: (agencyCode) => agentApi.post('/validate-agency-code', { agencyCode }),
  recordConsent: (agencyCode, consentGiven) => agentApi.post('/consent', { agencyCode, consentGiven }),
  setPassword: (agencyCode, password, confirmPassword) =>
    agentApi.post('/set-password', { agencyCode, password, confirmPassword }),
  setMpin: (agencyCode, mpin, confirmMpin) =>
    agentApi.post('/set-mpin', { agencyCode, mpin, confirmMpin }),
  verifyPassword: (agencyCode, password) =>
    agentApi.post('/verify-password', { agencyCode, password }),
  verifyMpin: (agencyCode, mpin) =>
    agentApi.post('/verify-mpin', { agencyCode, mpin }),
  getAgent: (agencyCode) => agentApi.get(`/${agencyCode}`),
};

export const otpService = {
  generate: (agencyCode) => otpApi.post('/generate', { agencyCode }),
  validate: (agencyCode, otpCode) => otpApi.post('/validate', { agencyCode, otpCode }),
  resend: (agencyCode) => otpApi.post('/resend', { agencyCode }),
};

export const loginService = {
  loginWithPassword: (agencyCode, password) =>
    loginApi.post('/password', { agencyCode, password }),
  loginWithMpin: (agencyCode, mpin) =>
    loginApi.post('/mpin', { agencyCode, mpin }),
};
