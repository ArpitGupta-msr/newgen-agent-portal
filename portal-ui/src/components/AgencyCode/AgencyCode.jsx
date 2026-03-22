import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { agentService, otpService } from '../../services/api';
import { ERRORS, SUCCESS } from '../../constants/messages';
import Logo from '../common/Logo';
import Stepper from '../common/Stepper';
import FormInput from '../common/FormInput';
import Button from '../common/Button';

function AgencyCode() {
  const [agencyCode, setAgencyCode] = useState('');
  const [consent, setConsent] = useState(false);
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [codeValidated, setCodeValidated] = useState(false);
  const [agentName, setAgentName] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const role = location.state?.role || 'AGENT';

  const handleValidateCode = async () => {
    if (!agencyCode.trim()) {
      setError(ERRORS.REQUIRED_FIELD('agency code'));
      return;
    }
    setLoading(true);
    setError('');
    try {
      const res = await agentService.validateAgencyCode(agencyCode);
      if (res.data.valid) {
        if (res.data.isRegistered) {
          navigate('/login', { state: { message: 'You are already registered. Please login.' } });
          return;
        }
        setCodeValidated(true);
        setAgentName(res.data.agentName || '');
      } else {
        setError(ERRORS.INVALID_AGENCY_CODE);
        setCodeValidated(false);
      }
    } catch (err) {
      setError(err.response?.data?.message || ERRORS.INVALID_AGENCY_CODE);
      setCodeValidated(false);
    } finally {
      setLoading(false);
    }
  };

  const handleRequestOtp = async () => {
    setLoading(true);
    setError('');
    try {
      await agentService.recordConsent(agencyCode, true);
      await otpService.generate(agencyCode);
      navigate('/otp-verification', { state: { agencyCode, role, agentName } });
    } catch (err) {
      setError(err.response?.data?.message || ERRORS.OTP_SEND_FAILED);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-card">
      <Logo />
      <Stepper currentStep={2} />

      <h2 className="h5 fw-semibold mb-1">Agency Verification</h2>
      <p className="text-muted small mb-4">Enter your agency code to proceed</p>

      <FormInput
        label="Agency Code"
        placeholder="Enter your agency code"
        value={agencyCode}
        onChange={(e) => {
          setAgencyCode(e.target.value);
          setCodeValidated(false);
          setError('');
        }}
        error={error}
        success={codeValidated ? SUCCESS.CODE_VERIFIED : ''}
      />

      {!codeValidated && (
        <Button
          variant="secondary"
          onClick={handleValidateCode}
          disabled={!agencyCode.trim()}
          loading={loading}
          style={{ marginBottom: 16 }}
        >
          {loading ? 'Validating...' : 'Validate Code'}
        </Button>
      )}

      {codeValidated && (
        <>
          <div className="form-check mb-3">
            <input
              type="checkbox"
              className="form-check-input"
              id="consent"
              checked={consent}
              onChange={(e) => setConsent(e.target.checked)}
            />
            <label className="form-check-label small" htmlFor="consent">
              I agree to the terms and conditions and acknowledge the privacy policy.
            </label>
          </div>

          <Button
            onClick={handleRequestOtp}
            disabled={!consent}
            loading={loading}
          >
            {loading ? 'Requesting OTP...' : 'Request for OTP'}
          </Button>
        </>
      )}
    </div>
  );
}

export default AgencyCode;
