import styles from './PageSizeSelect.module.css'

interface PageSizeSelectProps {
  value: number
  onChange: (value: number) => void
  options?: number[]
}

const DEFAULT_OPTIONS = [10, 20, 30, 50]

const PageSizeSelect = ({ value, onChange, options = DEFAULT_OPTIONS }: PageSizeSelectProps) => {
  return (
    <label className={styles.selectWrapper}>
      <span className={styles.label}>Por página</span>
      <select
        className={styles.select}
        value={value}
        onChange={(event) => onChange(Number(event.target.value))}
        aria-label="Seleccionar cantidad por página"
      >
        {options.map((option) => (
          <option key={option} value={option}>
            {option}
          </option>
        ))}
      </select>
    </label>
  )
}

export default PageSizeSelect
