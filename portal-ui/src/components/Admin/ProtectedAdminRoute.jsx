import { Navigate } from 'react-router-dom';

function ProtectedAdminRoute({ children }) {
  const isAdmin = !!sessionStorage.getItem('adminKey');
  return isAdmin ? children : <Navigate to="/admin/login" replace />;
}

export default ProtectedAdminRoute;
