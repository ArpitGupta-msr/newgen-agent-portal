import { useLocation, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { SUCCESS } from '../../constants/messages';
import Button from '../common/Button';
import styles from './Home.module.css';

function Home() {
  const location = useLocation();
  const navigate = useNavigate();
  const { agentName, agencyCode } = location.state || {};

  useEffect(() => {
    if (!agentName) navigate('/login');
  }, [agentName, navigate]);

  const initials = agentName
    ? agentName.split(' ').map((n) => n[0]).join('').toUpperCase()
    : '?';

  return (
    <div className="container">
      <div className={styles.wrapper}>
        <div className={styles.avatar}>{initials}</div>
        <h2>Welcome, {agentName}!</h2>
        <span className={styles.badge}>{agencyCode}</span>
        <p className="subtitle">{SUCCESS.LOGIN}</p>
        <Button variant="secondary" onClick={() => navigate('/')} style={{ marginTop: 16 }}>
          Logout
        </Button>
      </div>
    </div>
  );
}

export default Home;
