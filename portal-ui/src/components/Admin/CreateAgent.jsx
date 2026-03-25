import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminService } from '../../services/adminApi';
import FormInput from '../common/FormInput';
import Button from '../common/Button';
import ROLES from '../../constants/roles';

function CreateAgent() {
  const navigate = useNavigate();
  const [form, setForm] = useState({ agencyCode: '', name: '', role: 'AGENT' });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleChange = (field) => (e) => {
    setForm((prev) => ({ ...prev, [field]: e.target.value }));
    setError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!form.agencyCode.trim()) return setError('Please provide a valid agency code.');
    if (!form.name.trim()) return setError('Please provide a valid name.');

    try {
      setLoading(true);
      await adminService.createAgent(form.agencyCode.trim(), form.name.trim(), form.role);
      navigate('/admin');
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to create agent.';
      setError(msg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container py-4" style={{ maxWidth: 480 }}>
      <button className="btn btn-link ps-0 mb-3 text-decoration-none" onClick={() => navigate('/admin')}>
        &larr; Back
      </button>
      <h4 className="fw-semibold mb-4">Add New Agent</h4>

      {error && <div className="alert alert-danger">{error}</div>}

      <form onSubmit={handleSubmit}>
        <FormInput
          label="Agency Code"
          placeholder="e.g. AG005"
          value={form.agencyCode}
          onChange={handleChange('agencyCode')}
        />
        <FormInput
          label="Name"
          placeholder="Full name"
          value={form.name}
          onChange={handleChange('name')}
        />
        <div className="mb-3">
          <label className="form-label fw-medium">Role</label>
          <select
            className="form-select"
            value={form.role}
            onChange={handleChange('role')}
          >
            {ROLES.map((r) => (
              <option key={r.id} value={r.id}>{r.name} — {r.desc}</option>
            ))}
          </select>
        </div>
        <Button type="submit" loading={loading}>
          Create Agent
        </Button>
      </form>
    </div>
  );
}

export default CreateAgent;
