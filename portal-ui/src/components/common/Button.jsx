function Button({ children, variant = 'primary', disabled, onClick, type = 'button', loading, style }) {
  const variantMap = {
    primary: 'btn-primary',
    secondary: 'btn-outline-secondary',
    link: 'btn-link',
  };
  const btnClass = variantMap[variant] || 'btn-primary';
  const widthClass = variant === 'link' ? '' : 'w-100';

  return (
    <button
      type={type}
      className={`btn ${btnClass} ${widthClass}`}
      disabled={disabled || loading}
      onClick={onClick}
      style={style}
    >
      {loading && (
        <span className="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true" />
      )}
      {children}
    </button>
  );
}

export default Button;
