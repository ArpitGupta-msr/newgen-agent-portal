import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Welcome from './components/Welcome/Welcome';
import AgencyCode from './components/AgencyCode/AgencyCode';
import OtpVerification from './components/OtpVerification/OtpVerification';
import SetCredential from './components/SetCredential/SetCredential';
import SetPassword from './components/SetPassword/SetPassword';
import SetMpin from './components/SetMpin/SetMpin';
import Login from './components/Login/Login';
import Home from './components/Home/Home';
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
      </Routes>
    </Router>
  );
}

export default App;
