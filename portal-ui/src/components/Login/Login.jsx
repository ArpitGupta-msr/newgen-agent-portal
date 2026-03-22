import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { loginService } from '../../services/api';
import { ERRORS } from '../../constants/messages';
import { MPIN_LENGTH } from '../../constants/validation';
import Logo from '../common/Logo';
import FormInput from '../common/FormInput';
import Button from '../common/Button';

function Login() {
  const [loginMethod, setLoginMethod] = useState('password');
  const [agencyCode, setAgencyCode] = useState('');
  const [password, setPassword] = useState('');
  const [mpin, setMpin] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleMpinChange = (value) => {
    if (/^\d{0,4}$/.test(value)) {
      setMpin(value);
      setError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!agencyCode.trim()) {
      setError(ERRORS.REQUIRED_FIELD('agency code'));
      return;
    }

    setLoading(true);
    try {
      let res;
      if (loginMethod === 'password') {
        if (!password) {
          setError(ERRORS.REQUIRED_FIELD('password'));
          setLoading(false);
          return;
        }
        res = await loginService.loginWithPassword(agencyCode, password);
      } else {
        if (mpin.length !== MPIN_LENGTH) {
          setError(ERRORS.REQUIRED_FIELD('4-digit MPIN'));
          setLoading(false);
          return;
        }
        res = await loginService.loginWithMpin(agencyCode, mpin);
      }

      if (res.data.success) {
        navigate('/home', { state: { agentName: res.data.agentName, agencyCode } });
      } else {
        setError(res.data.message || 'Login failed. Please try again.');
      }
    } catch (err) {
      const msg = err.response?.data?.message;
      setError(msg || (loginMethod === 'password' ? ERRORS.INCORRECT_PASSWORD : ERRORS.INCORRECT_MPIN));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="page-card">
      <Logo />

      <h2 className="h5 fw-semibold mb-1">Login</h2>
      <p className="text-muted small mb-4">Access your account securely</p>

      <ul className="nav nav-tabs mb-4">
        <li className="nav-item">
          <button
            className={`nav-link ${loginMethod === 'password' ? 'active' : ''}`}
            onClick={() => { setLoginMethod('password'); setError(''); }}
          >
            Password
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${loginMethod === 'mpin' ? 'active' : ''}`}
            onClick={() => { setLoginMethod('mpin'); setError(''); }}
          >
            MPIN
          </button>
        </li>
      </ul>

      <form onSubmit={handleSubmit}>
        <FormInput
          label="Agency Code"
          placeholder="Enter your agency code"
          value={agencyCode}
          onChange={(e) => { setAgencyCode(e.target.value); setError(''); }}
        />

        {loginMethod === 'password' ? (
          <FormInput
            label="Password"
            type="password"
            placeholder="Enter your password"
            value={password}
            onChange={(e) => { setPassword(e.target.value); setError(''); }}
          />
        ) : (
          <FormInput
            label="MPIN"
            type="password"
            inputMode="numeric"
            placeholder="Enter 4-digit MPIN"
            maxLength={4}
            value={mpin}
            onChange={(e) => handleMpinChange(e.target.value)}
          />
        )}

        {error && <div className="text-danger small mb-3">{error}</div>}

        <Button type="submit" loading={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </Button>
      </form>

      <div className="text-center mt-3 small">
        Don&apos;t have an account? <Link to="/">Sign Up</Link>
      </div>
    </div>
  );
}

export default Login;
