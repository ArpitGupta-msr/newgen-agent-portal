import { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import Logo from '../common/Logo';
import Stepper from '../common/Stepper';
import Button from '../common/Button';

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
    <div className="page-card">
      <Logo />
      <Stepper currentStep={4} />

      <h2 className="h5 fw-semibold mb-1">Set Up Credentials</h2>
      <p className="text-muted small mb-4">Choose your preferred login method</p>

      <ul className="nav nav-tabs mb-4">
        <li className="nav-item">
          <button
            className={`nav-link ${credType === 'password' ? 'active' : ''}`}
            onClick={() => setCredType('password')}
          >
            Password
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${credType === 'mpin' ? 'active' : ''}`}
            onClick={() => setCredType('mpin')}
          >
            MPIN
          </button>
        </li>
      </ul>

      <p className="text-muted small mb-4">
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
