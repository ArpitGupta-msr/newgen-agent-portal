import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import Logo from '../common/Logo';
import Stepper from '../common/Stepper';
import Button from '../common/Button';
import ROLES, { DEFAULT_ROLE } from '../../constants/roles';
import styles from './Welcome.module.css';

function Welcome() {
  const [selectedRole, setSelectedRole] = useState(DEFAULT_ROLE);
  const navigate = useNavigate();

  const handleGetStarted = () => {
    navigate('/signup', { state: { role: selectedRole } });
  };

  return (
    <div className="container">
      <Logo />
      <Stepper currentStep={1} />

      <h2>Welcome!</h2>
      <p className="subtitle">Select your role to get started</p>

      <div className={styles.roleGrid}>
        {ROLES.map((role) => (
          <div
            key={role.id}
            className={`${styles.roleCard} ${selectedRole === role.id ? styles.selected : ''}`}
            onClick={() => setSelectedRole(role.id)}
          >
            <div className={styles.roleName}>{role.name}</div>
            <p className={styles.roleDesc}>{role.desc}</p>
          </div>
        ))}
      </div>

      <Button onClick={handleGetStarted}>Get Started</Button>

      <div className={styles.navLinks}>
        Already have an account? <Link to="/login">Login</Link>
      </div>
    </div>
  );
}

export default Welcome;
