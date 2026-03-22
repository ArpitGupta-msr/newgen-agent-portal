import styles from './Stepper.module.css';

const TOTAL_STEPS = 4;

function Stepper({ currentStep }) {
  return (
    <div className={styles.stepper}>
      {Array.from({ length: TOTAL_STEPS }, (_, i) => {
        const step = i + 1;
        let className = styles.step;
        if (step < currentStep) className += ` ${styles.completed}`;
        else if (step === currentStep) className += ` ${styles.active}`;
        return <div key={step} className={className} />;
      })}
    </div>
  );
}

export default Stepper;
