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
    <div className="mb-3">
      {label && <label className="form-label fw-medium">{label}</label>}
      <input
        type={type}
        placeholder={placeholder}
        value={value}
        onChange={onChange}
        className={`form-control ${error ? 'is-invalid' : success ? 'is-valid' : ''}`}
        inputMode={inputMode}
        maxLength={maxLength}
        autoFocus={autoFocus}
      />
      {error && <div className="invalid-feedback">{error}</div>}
      {success && <div className="valid-feedback">{success}</div>}
    </div>
  );
}

export default FormInput;
