import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Logo from '../common/Logo';
import Stepper from '../common/Stepper';
import Button from '../common/Button';
import styles from './SetCredential.module.css';

function SetCredential() {
  const [credType, setCredType] = useState('password');
  const navigate = useNavigate();
  const location = useLocation();
  const { agencyCode, role, agentName } = location.state || {};

  const handleProceed = () => {
    const state = { agencyCode, role, agentName };
    navigate(credType === 'password' ? '/set-password' : '/set-mpin', { state });
  };

  return (
    <div className="container">
      <Logo />
      <Stepper currentStep={4} />

      <h2>Set Up Credentials</h2>
      <p className="subtitle">Choose your preferred login method</p>

      <div className={styles.tabs}>
        <button
          className={`${styles.tab} ${credType === 'password' ? styles.active : ''}`}
          onClick={() => setCredType('password')}
        >
          Password
        </button>
        <button
          className={`${styles.tab} ${credType === 'mpin' ? styles.active : ''}`}
          onClick={() => setCredType('mpin')}
        >
          MPIN
        </button>
      </div>

      <p className={styles.description}>
        {credType === 'password'
          ? 'Set a strong password with uppercase, lowercase, numbers, and symbols.'
          : 'Set a 4-digit numeric MPIN for quick and easy login.'}
      </p>

      <Button onClick={handleProceed}>
        Continue with {credType === 'password' ? 'Password' : 'MPIN'}
      </Button>
    </div>
  );
}

export default SetCredential;
