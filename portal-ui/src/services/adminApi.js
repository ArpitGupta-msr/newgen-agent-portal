import axios from 'axios';

const ADMIN_KEY_HEADER = 'X-Admin-Key';

const adminApi = axios.create({
  baseURL: '/newgen/admin/agents',
  headers: { 'Content-Type': 'application/json' },
});

adminApi.interceptors.request.use((config) => {
  const key = sessionStorage.getItem('adminKey');
  if (key) config.headers[ADMIN_KEY_HEADER] = key;
  return config;
});

export const adminService = {
  createAgent: (agencyCode, name, role) =>
    adminApi.post('', { agencyCode, name, role }),
  getAllAgents: () => adminApi.get(''),
  getAgent: (agencyCode) => adminApi.get(`/${agencyCode}`),
  deleteAgent: (agencyCode) => adminApi.delete(`/${agencyCode}`),
};
