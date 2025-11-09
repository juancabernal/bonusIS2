import React from 'react';

type Props = { children: React.ReactNode };
type State = { hasError: boolean; message?: string };

export default class AppErrorBoundary extends React.Component<Props, State> {
  state: State = { hasError: false, message: '' };

  static getDerivedStateFromError(error: unknown) {
    return { hasError: true, message: error instanceof Error ? error.message : String(error) };
  }

  componentDidCatch(error: unknown, info: unknown) {
    // Log útil en dev
    // eslint-disable-next-line no-console
    console.error('AppErrorBoundary', { error, info });
  }

  render() {
    if (this.state.hasError) {
      return (
        <main style={{ padding: 24 }}>
          <h1>Ocurrió un error en la aplicación</h1>
          <pre style={{ whiteSpace: 'pre-wrap' }}>{this.state.message}</pre>
        </main>
      );
    }
    return this.props.children;
  }
}
