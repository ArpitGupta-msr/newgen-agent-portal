import { useLocation, useNavigate } from 'react-router-dom';
import { useEffect } from 'react';
import { SUCCESS } from '../../constants/messages';
import Button from '../common/Button';

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
    <div className="page-card text-center">
      <div
        className="rounded-circle bg-primary text-white d-inline-flex align-items-center justify-content-center mb-3"
        style={{ width: 80, height: 80, fontSize: '1.5rem', fontWeight: 600 }}
      >
        {initials}
      </div>
      <h2 className="h5 fw-semibold mb-2">Welcome, {agentName}!</h2>
      <span className="badge bg-secondary mb-3">{agencyCode}</span>
      <p className="text-muted small">{SUCCESS.LOGIN}</p>
      <Button variant="secondary" onClick={() => navigate('/')} style={{ marginTop: 16 }}>
        Logout
      </Button>
    </div>
  );
}

export default Home;
