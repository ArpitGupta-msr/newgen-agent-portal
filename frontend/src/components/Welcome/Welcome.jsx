import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import Logo from '../common/Logo';
import Stepper from '../common/Stepper';
import Button from '../common/Button';
import ROLES, { DEFAULT_ROLE } from '../../constants/roles';

function Welcome() {
  const [selectedRole, setSelectedRole] = useState(DEFAULT_ROLE);
  const navigate = useNavigate();

  const handleGetStarted = () => {
    navigate('/signup', { state: { role: selectedRole } });
  };

  return (
    <div className="page-card">
      <Logo />
      <Stepper currentStep={1} />

      <h2 className="h5 fw-semibold mb-1">Welcome!</h2>
      <p className="text-muted small mb-4">Select your role to get started</p>

      <div className="row row-cols-2 g-3 mb-4">
        {ROLES.map((role) => (
          <div key={role.id} className="col">
            <div
              className={`card h-100 ${selectedRole === role.id ? 'border-primary' : ''}`}
              onClick={() => setSelectedRole(role.id)}
              style={{ cursor: 'pointer', backgroundColor: selectedRole === role.id ? 'rgba(102,126,234,0.08)' : '' }}
            >
              <div className="card-body text-center p-3">
                <div className="fw-bold small">{role.name}</div>
                <p className="text-muted mb-0" style={{ fontSize: '0.75rem' }}>{role.desc}</p>
              </div>
            </div>
          </div>
        ))}
      </div>

      <Button onClick={handleGetStarted}>Get Started</Button>

      <div className="text-center mt-3 small">
        Already have an account? <Link to="/login">Login</Link>
      </div>
    </div>
  );
}

export default Welcome;
