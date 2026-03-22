import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { agentService } from '../../services/api';
import { ERRORS, SUCCESS } from '../../constants/messages';
import { PASSWORD_RULES } from '../../constants/validation';
import Logo from '../common/Logo';
import Stepper from '../common/Stepper';
import FormInput from '../common/FormInput';
import Button from '../common/Button';
import SuccessScreen from '../common/SuccessScreen';

function SetPassword() {
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { agencyCode } = location.state || {};

  const allChecksPass = password && PASSWORD_RULES.every((c) => c.test(password));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (!allChecksPass) {
      setError(ERRORS.PASSWORD_CRITERIA);
      return;
    }
    if (password !== confirmPassword) {
      setError(ERRORS.PASSWORD_MISMATCH);
      return;
    }

    setLoading(true);
    try {
      const res = await agentService.setPassword(agencyCode, password, confirmPassword);
      if (res.data.success) {
        setSuccess(true);
      } else {
        setError(res.data.message || ERRORS.SET_PASSWORD_FAILED);
      }
    } catch (err) {
      setError(err.response?.data?.message || ERRORS.SET_PASSWORD_FAILED);
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <SuccessScreen
        title="Password Set!"
        message={SUCCESS.PASSWORD_SET}
        buttonText="Proceed to Login"
        onButtonClick={() => navigate('/login')}
      />
    );
  }

  return (
    <div className="page-card">
      <Logo />
      <Stepper currentStep={4} />

      <h2 className="h5 fw-semibold mb-1">Set Password</h2>
      <p className="text-muted small mb-4">Create a strong password for your account</p>

      <form onSubmit={handleSubmit}>
        <FormInput
          label="Password"
          type="password"
          placeholder="Enter password"
          value={password}
          onChange={(e) => { setPassword(e.target.value); setError(''); }}
        />

        {password && (
          <div className="mb-3">
            {PASSWORD_RULES.map((check, i) => (
              <div key={i} className={`small ${check.test(password) ? 'text-success' : 'text-danger'}`}>
                {check.test(password) ? '\u2713' : '\u2717'} {check.label}
              </div>
            ))}
          </div>
        )}

        <FormInput
          label="Confirm Password"
          type="password"
          placeholder="Confirm password"
          value={confirmPassword}
          onChange={(e) => { setConfirmPassword(e.target.value); setError(''); }}
          error={confirmPassword && password !== confirmPassword ? ERRORS.PASSWORD_MISMATCH : ''}
        />

        {error && <div className="text-danger small mb-3">{error}</div>}

        <Button
          type="submit"
          disabled={!allChecksPass || password !== confirmPassword}
          loading={loading}
        >
          {loading ? 'Setting Password...' : 'Set Password'}
        </Button>
      </form>
    </div>
  );
}

export default SetPassword;
