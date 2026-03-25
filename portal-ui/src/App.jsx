import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Welcome from './components/Welcome/Welcome';
import AgencyCode from './components/AgencyCode/AgencyCode';
import OtpVerification from './components/OtpVerification/OtpVerification';
import SetCredential from './components/SetCredential/SetCredential';
import SetPassword from './components/SetPassword/SetPassword';
import SetMpin from './components/SetMpin/SetMpin';
import Login from './components/Login/Login';
import Home from './components/Home/Home';
import AdminDashboard from './components/Admin/AdminDashboard';
import CreateAgent from './components/Admin/CreateAgent';
import AdminLogin from './components/Admin/AdminLogin';
import ProtectedAdminRoute from './components/Admin/ProtectedAdminRoute';
import './styles/App.css';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Welcome />} />
        <Route path="/signup" element={<AgencyCode />} />
        <Route path="/otp-verification" element={<OtpVerification />} />
        <Route path="/set-credential" element={<SetCredential />} />
        <Route path="/set-password" element={<SetPassword />} />
        <Route path="/set-mpin" element={<SetMpin />} />
        <Route path="/login" element={<Login />} />
        <Route path="/home" element={<Home />} />
        <Route path="/admin/login" element={<AdminLogin />} />
        <Route path="/admin" element={<ProtectedAdminRoute><AdminDashboard /></ProtectedAdminRoute>} />
        <Route path="/admin/create" element={<ProtectedAdminRoute><CreateAgent /></ProtectedAdminRoute>} />
      </Routes>
    </Router>
  );
}

export default App;
