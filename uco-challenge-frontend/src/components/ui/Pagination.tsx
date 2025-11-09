import styles from './Pagination.module.css'

interface PaginationProps {
  page: number
  size: number
  totalElements: number
  onPageChange: (newPage: number) => void
}

const MAX_VISIBLE_PAGES = 5

const Pagination = ({ page, size, totalElements, onPageChange }: PaginationProps) => {
  const totalPages = Math.max(1, Math.ceil(totalElements / Math.max(size, 1)))
  const clampedPage = Math.min(Math.max(page, 0), totalPages - 1)

  const startItem = totalElements === 0 ? 0 : clampedPage * size + 1
  const endItem = totalElements === 0 ? 0 : Math.min(totalElements, (clampedPage + 1) * size)

  const handlePageChange = (nextPage: number) => {
    if (nextPage === clampedPage) return
    if (nextPage < 0 || nextPage > totalPages - 1) return
    onPageChange(nextPage)
  }

  const halfWindow = Math.floor(MAX_VISIBLE_PAGES / 2)
  let start = Math.max(clampedPage - halfWindow, 0)
  let end = start + MAX_VISIBLE_PAGES - 1

  if (end > totalPages - 1) {
    end = totalPages - 1
    start = Math.max(end - MAX_VISIBLE_PAGES + 1, 0)
  }

  const pageNumbers: number[] = []
  for (let i = start; i <= end; i += 1) {
    pageNumbers.push(i)
  }

  return (
    <div className={styles.container}>
      <p className={styles.summary} aria-live="polite">
        Mostrando {startItem}–{endItem} de {totalElements}
      </p>
      <nav className={styles.controls} aria-label="Paginación">
        <button
          type="button"
          className={styles.button}
          onClick={() => handlePageChange(clampedPage - 1)}
          disabled={clampedPage === 0}
          aria-label="Ir a la página anterior"
          title="Ir a la página anterior"
        >
          « Anterior
        </button>
        {pageNumbers.map((pageNumber) => (
          <button
            type="button"
            key={pageNumber}
            className={`${styles.button} ${pageNumber === clampedPage ? styles.active : ''}`.trim()}
            onClick={() => handlePageChange(pageNumber)}
            aria-current={pageNumber === clampedPage ? 'page' : undefined}
            aria-label={`Ir a la página ${pageNumber + 1}`}
            title={`Ir a la página ${pageNumber + 1}`}
          >
            {pageNumber + 1}
          </button>
        ))}
        <button
          type="button"
          className={styles.button}
          onClick={() => handlePageChange(clampedPage + 1)}
          disabled={(clampedPage + 1) * size >= totalElements}
          aria-label="Ir a la página siguiente"
          title="Ir a la página siguiente"
        >
          Siguiente »
        </button>
      </nav>
    </div>
  )
}

export default Pagination
