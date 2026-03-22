import { useState, useRef, useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { otpService } from '../../services/api';
import { ERRORS, SUCCESS } from '../../constants/messages';
import { OTP_LENGTH, MAX_OTP_RESENDS, OTP_RESEND_INTERVAL_SECONDS } from '../../constants/validation';
import Logo from '../common/Logo';
import Stepper from '../common/Stepper';
import Button from '../common/Button';
import SuccessScreen from '../common/SuccessScreen';
import styles from './OtpVerification.module.css';

function OtpVerification() {
  const [otp, setOtp] = useState(Array(OTP_LENGTH).fill(''));
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [resendCount, setResendCount] = useState(0);
  const [resendTimer, setResendTimer] = useState(0);
  const [verified, setVerified] = useState(false);
  const [verifiedName, setVerifiedName] = useState('');
  const inputRefs = useRef([]);
  const navigate = useNavigate();
  const location = useLocation();
  const { agencyCode, role, agentName } = location.state || {};

  useEffect(() => {
    if (!agencyCode) navigate('/');
  }, [agencyCode, navigate]);

  useEffect(() => {
    if (resendTimer > 0) {
      const timer = setTimeout(() => setResendTimer(resendTimer - 1), 1000);
      return () => clearTimeout(timer);
    }
  }, [resendTimer]);

  const resetOtpInputs = () => {
    setOtp(Array(OTP_LENGTH).fill(''));
    inputRefs.current[0]?.focus();
  };

  const handleOtpChange = (index, value) => {
    if (!/^\d*$/.test(value)) return;
    const newOtp = [...otp];
    newOtp[index] = value.slice(-1);
    setOtp(newOtp);
    setError('');
    if (value && index < OTP_LENGTH - 1) {
      inputRefs.current[index + 1]?.focus();
    }
  };

  const handleKeyDown = (index, e) => {
    if (e.key === 'Backspace' && !otp[index] && index > 0) {
      inputRefs.current[index - 1]?.focus();
    }
  };

  const handlePaste = (e) => {
    e.preventDefault();
    const pasted = e.clipboardData.getData('text').replace(/\D/g, '').slice(0, OTP_LENGTH);
    if (pasted.length > 0) {
      const newOtp = Array(OTP_LENGTH).fill('');
      for (let i = 0; i < pasted.length; i++) newOtp[i] = pasted[i];
      setOtp(newOtp);
      inputRefs.current[Math.min(pasted.length, OTP_LENGTH - 1)]?.focus();
    }
  };

  const handleVerify = async () => {
    const otpCode = otp.join('');
    if (otpCode.length < OTP_LENGTH) {
      setError(ERRORS.OTP_INCOMPLETE);
      return;
    }
    setLoading(true);
    setError('');
    try {
      const res = await otpService.validate(agencyCode, otpCode);
      if (res.data.valid) {
        setVerified(true);
        setVerifiedName(res.data.agentName || agentName || '');
      } else {
        setError(ERRORS.INVALID_OTP);
        resetOtpInputs();
      }
    } catch (err) {
      setError(err.response?.data?.message || ERRORS.INVALID_OTP);
      resetOtpInputs();
    } finally {
      setLoading(false);
    }
  };

  const handleResend = async () => {
    if (resendCount >= MAX_OTP_RESENDS) return;
    setLoading(true);
    setError('');
    try {
      await otpService.resend(agencyCode);
      setResendCount(resendCount + 1);
      setResendTimer(OTP_RESEND_INTERVAL_SECONDS);
      resetOtpInputs();
    } catch (err) {
      setError(err.response?.data?.message || ERRORS.OTP_RESEND_FAILED);
    } finally {
      setLoading(false);
    }
  };

  if (verified) {
    return (
      <SuccessScreen
        title="Verified!"
        message={SUCCESS.OTP_VERIFIED(verifiedName)}
        buttonText="Next"
        onButtonClick={() =>
          navigate('/set-credential', { state: { agencyCode, role, agentName: verifiedName } })
        }
      />
    );
  }

  const formatTimer = (seconds) => {
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return `${m}:${s.toString().padStart(2, '0')}`;
  };

  return (
    <div className="container">
      <Logo />
      <Stepper currentStep={3} />

      <h2>OTP Verification</h2>
      <p className="subtitle">Enter the 6-digit code sent to your registered device</p>

      <div className={styles.otpGroup} onPaste={handlePaste}>
        {otp.map((digit, index) => (
          <input
            key={index}
            ref={(el) => (inputRefs.current[index] = el)}
            type="text"
            inputMode="numeric"
            maxLength={1}
            value={digit}
            onChange={(e) => handleOtpChange(index, e.target.value)}
            onKeyDown={(e) => handleKeyDown(index, e)}
            autoFocus={index === 0}
          />
        ))}
      </div>

      {error && <div className={styles.error}>{error}</div>}

      <div className={styles.resendRow}>
        <span>
          {resendCount >= MAX_OTP_RESENDS
            ? 'Max resend attempts reached'
            : resendTimer > 0
            ? `Resend in ${formatTimer(resendTimer)}`
            : `Resend available (${MAX_OTP_RESENDS - resendCount} left)`}
        </span>
        <Button
          variant="link"
          onClick={handleResend}
          disabled={resendCount >= MAX_OTP_RESENDS || resendTimer > 0 || loading}
        >
          Resend OTP
        </Button>
      </div>

      <Button
        onClick={handleVerify}
        disabled={otp.join('').length < OTP_LENGTH}
        loading={loading}
      >
        {loading ? 'Verifying...' : 'Verify OTP'}
      </Button>
    </div>
  );
}

export default OtpVerification;
