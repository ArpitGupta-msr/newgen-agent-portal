import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminService } from '../../services/adminApi';
import Button from '../common/Button';

function AdminDashboard() {
  const navigate = useNavigate();
  const [agents, setAgents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deletingCode, setDeletingCode] = useState(null);

  const fetchAgents = async () => {
    try {
      setLoading(true);
      const res = await adminService.getAllAgents();
      setAgents(res.data);
    } catch {
      setError('Failed to load agents.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAgents();
  }, []);

  const handleDelete = async (agencyCode) => {
    if (!window.confirm(`Delete agent ${agencyCode}?`)) return;
    try {
      setDeletingCode(agencyCode);
      await adminService.deleteAgent(agencyCode);
      setAgents((prev) => prev.filter((a) => a.agencyCode !== agencyCode));
    } catch {
      setError('Failed to delete agent.');
    } finally {
      setDeletingCode(null);
    }
  };

  return (
    <div className="container py-4">
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h4 className="fw-semibold mb-0">Admin — Agent Management</h4>
        <Button onClick={() => navigate('/admin/create')} style={{ width: 'auto' }}>
          + Add Agent
        </Button>
      </div>

      {error && <div className="alert alert-danger">{error}</div>}

      {loading ? (
        <div className="text-center py-5">
          <span className="spinner-border text-primary" />
        </div>
      ) : agents.length === 0 ? (
        <div className="text-center text-muted py-5">
          No agents found. Add one to get started.
        </div>
      ) : (
        <div className="table-responsive">
          <table className="table table-bordered table-hover align-middle">
            <thead className="table-light">
              <tr>
                <th>Agency Code</th>
                <th>Name</th>
                <th>Role</th>
                <th>Consent</th>
                <th>Registered</th>
                <th>Created</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {agents.map((agent) => (
                <tr key={agent.agencyCode}>
                  <td><code>{agent.agencyCode}</code></td>
                  <td>{agent.name}</td>
                  <td><span className="badge bg-secondary">{agent.role}</span></td>
                  <td>
                    {agent.consentGiven
                      ? <span className="badge bg-success">Yes</span>
                      : <span className="badge bg-light text-dark">No</span>}
                  </td>
                  <td>
                    {agent.isRegistered
                      ? <span className="badge bg-success">Yes</span>
                      : <span className="badge bg-warning text-dark">Pending</span>}
                  </td>
                  <td className="text-muted small">
                    {new Date(agent.createdAt).toLocaleDateString()}
                  </td>
                  <td>
                    <button
                      className="btn btn-sm btn-outline-danger"
                      disabled={deletingCode === agent.agencyCode}
                      onClick={() => handleDelete(agent.agencyCode)}
                    >
                      {deletingCode === agent.agencyCode ? '...' : 'Delete'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
}

export default AdminDashboard;
