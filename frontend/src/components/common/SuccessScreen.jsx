import Button from './Button';

function SuccessScreen({ title, message, buttonText, onButtonClick }) {
  return (
    <div className="page-card text-center">
      <div className="display-4 text-success mb-3">&#10003;</div>
      <h2 className="h4 mb-2">{title}</h2>
      <p className="text-muted mb-4">{message}</p>
      <Button onClick={onButtonClick}>{buttonText}</Button>
    </div>
  );
}

export default SuccessScreen;
