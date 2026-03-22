import Button from './Button';
import styles from './SuccessScreen.module.css';

function SuccessScreen({ title, message, buttonText, onButtonClick }) {
  return (
    <div className="container">
      <div className={styles.wrapper}>
        <div className={styles.checkmark}>&#10003;</div>
        <h2 className={styles.title}>{title}</h2>
        <p className={styles.message}>{message}</p>
        <Button onClick={onButtonClick}>{buttonText}</Button>
      </div>
    </div>
  );
}

export default SuccessScreen;
