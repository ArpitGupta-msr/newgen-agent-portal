import { useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { agentService } from '../../services/api';
import { ERRORS, SUCCESS } from '../../constants/messages';
import { MPIN_LENGTH } from '../../constants/validation';
import Logo from '../common/Logo';
import Stepper from '../common/Stepper';
import FormInput from '../common/FormInput';
import Button from '../common/Button';
import SuccessScreen from '../common/SuccessScreen';

function SetMpin() {
  const [mpin, setMpin] = useState('');
  const [confirmMpin, setConfirmMpin] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();
  const location = useLocation();
  const { agencyCode } = location.state || {};

  const handleMpinInput = (value, setter) => {
    if (/^\d{0,4}$/.test(value)) {
      setter(value);
      setError('');
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    if (mpin.length !== MPIN_LENGTH) {
      setError(ERRORS.MPIN_FORMAT);
      return;
    }
    if (mpin !== confirmMpin) {
      setError(ERRORS.MPIN_MISMATCH);
      return;
    }

    setLoading(true);
    try {
      const res = await agentService.setMpin(agencyCode, mpin, confirmMpin);
      if (res.data.success) {
        setSuccess(true);
      } else {
        setError(res.data.message || ERRORS.SET_MPIN_FAILED);
      }
    } catch (err) {
      setError(err.response?.data?.message || ERRORS.SET_MPIN_FAILED);
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <SuccessScreen
        title="MPIN Set!"
        message={SUCCESS.MPIN_SET}
        buttonText="Proceed to Login"
        onButtonClick={() => navigate('/login')}
      />
    );
  }

  return (
    <div className="page-card">
      <Logo />
      <Stepper currentStep={4} />

      <h2 className="h5 fw-semibold mb-1">Set MPIN</h2>
      <p className="text-muted small mb-4">Create a 4-digit MPIN for quick login</p>

      <form onSubmit={handleSubmit}>
        <FormInput
          label="MPIN"
          type="password"
          inputMode="numeric"
          placeholder="Enter 4-digit MPIN"
          maxLength={4}
          value={mpin}
          onChange={(e) => handleMpinInput(e.target.value, setMpin)}
        />

        <FormInput
          label="Confirm MPIN"
          type="password"
          inputMode="numeric"
          placeholder="Confirm 4-digit MPIN"
          maxLength={4}
          value={confirmMpin}
          onChange={(e) => handleMpinInput(e.target.value, setConfirmMpin)}
          error={confirmMpin && mpin !== confirmMpin ? ERRORS.MPIN_MISMATCH : ''}
        />

        {error && <div className="text-danger small mb-3">{error}</div>}

        <Button
          type="submit"
          disabled={mpin.length !== MPIN_LENGTH || mpin !== confirmMpin}
          loading={loading}
        >
          {loading ? 'Setting MPIN...' : 'Set MPIN'}
        </Button>
      </form>
    </div>
  );
}

export default SetMpin;
