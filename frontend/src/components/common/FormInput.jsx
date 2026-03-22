import styles from './FormInput.module.css';

function FormInput({
  label,
  type = 'text',
  placeholder,
  value,
  onChange,
  error,
  success,
  inputMode,
  maxLength,
  autoFocus,
}) {
  return (
    <div className={styles.group}>
      {label && <label className={styles.label}>{label}</label>}
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        className={`${styles.input} ${error ? styles.inputError : ''}`}
        inputMode={inputMode}
        maxLength={maxLength}
        autoFocus={autoFocus}
      />
      {error && <div className={styles.error}>{error}</div>}
      {success && <div className={styles.success}>{success}</div>}
    </div>
  );
}

export default FormInput;
