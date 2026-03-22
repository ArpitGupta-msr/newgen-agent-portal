import styles from './Button.module.css';

function Button({ children, variant = 'primary', disabled, onClick, type = 'button', loading, style }) {
  const className = `${styles.btn} ${styles[variant] || styles.primary}`;

  return (
    <button
      type={type}
      className={className}
      disabled={disabled || loading}
      onClick={onClick}
      style={style}
    >
      {loading && <span className={styles.spinner} />}
      {children}
    </button>
  );
}

export default Button;
