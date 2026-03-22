import styles from './Logo.module.css';

function Logo() {
  return (
    <div className={styles.logo}>
      <h1 className={styles.title}>NewGen Insurance</h1>
      <span className={styles.subtitle}>Agent Portal</span>
    </div>
  );
}

export default Logo;
