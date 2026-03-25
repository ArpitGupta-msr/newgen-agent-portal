import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import FormInput from '../common/FormInput';
import Button from '../common/Button';

const ADMIN_USERNAME = 'admin';
const ADMIN_PASSWORD = 'Admin@1234';
const ADMIN_SECRET_KEY = 'newgen-admin-secret-2024';

function AdminLogin() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ username: '', password: '' });
  const [error, setError] = useState('');

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
    setError('');
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    if (form.username === ADMIN_USERNAME && form.password === ADMIN_PASSWORD) {
      sessionStorage.setItem('adminKey', ADMIN_SECRET_KEY);
      navigate('/admin');
    } else {
      setError('Invalid admin credentials.');
    }
  };

  return (
    <div className="d-flex justify-content-center align-items-center vh-100 bg-light">
      <div className="card shadow-sm p-4" style={{ width: 360 }}>
        <h5 className="fw-semibold mb-1">Admin Login</h5>
        <p className="text-muted small mb-4">NewGen Insurance Portal</p>

        {error && <div className="alert alert-danger py-2">{error}</div>}

        <form onSubmit={handleSubmit}>
          <FormInput
            label="Username"
            placeholder="admin"
            value={form.username}
            onChange={handleChange('username')}
            autoFocus
          />
          <FormInput
            label="Password"
            type="password"
            placeholder="Password"
            value={form.password}
            onChange={handleChange('password')}
          />
          <Button type="submit">Login</Button>
        </form>
      </div>
    </div>
  );
}

export default AdminLogin;
