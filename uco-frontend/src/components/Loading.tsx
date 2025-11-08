interface LoadingProps {
  message?: string
}

const Loading = ({ message = 'Cargando...' }: LoadingProps) => (
  <div className="loader" role="status" aria-live="polite">
    <div className="loader__spinner" aria-hidden />
    <p>{message}</p>
  </div>
)

export default Loading
