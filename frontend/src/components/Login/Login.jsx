import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { loginService } from '../../services/api';
import { ERRORS } from '../../constants/messages';
import { MPIN_LENGTH } from '../../constants/validation';
import Logo from '../common/Logo';
import FormInput from '../common/FormInput';
import Button from '../common/Button';
import styles from './Login.module.css';

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
    <div className="container">
      <Logo />

      <h2>Login</h2>
      <p className="subtitle">Access your account securely</p>

      <div className={styles.tabs}>
        <button
          className={`${styles.tab} ${loginMethod === 'password' ? styles.active : ''}`}
          onClick={() => { setLoginMethod('password'); setError(''); }}
        >
          Password
        </button>
        <button
          className={`${styles.tab} ${loginMethod === 'mpin' ? styles.active : ''}`}
          onClick={() => { setLoginMethod('mpin'); setError(''); }}
        >
          MPIN
        </button>
      </div>

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

        {error && <div className={styles.error}>{error}</div>}

        <Button type="submit" loading={loading}>
          {loading ? 'Logging in...' : 'Login'}
        </Button>
      </form>

      <div className={styles.navLinks}>
        Don't have an account? <Link to="/">Sign Up</Link>
      </div>
    </div>
  );
}

export default Login;
