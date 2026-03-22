const TOTAL_STEPS = 4;

function Stepper({ currentStep }) {
  return (
    <div className="d-flex gap-2 mb-4">
      {Array.from({ length: TOTAL_STEPS }, (_, i) => {
        const step = i + 1;
        const bgClass =
          step < currentStep
            ? 'bg-success'
            : step === currentStep
            ? 'bg-primary'
            : 'bg-secondary bg-opacity-25';
        return <div key={step} className={`flex-fill rounded-pill ${bgClass}`} style={{ height: 6 }} />;
      })}
    </div>
  );
}

export default Stepper;
