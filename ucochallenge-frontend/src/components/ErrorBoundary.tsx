import React from 'react';

export default class ErrorBoundary extends React.Component<any, any> {
  constructor(props: any) { super(props); this.state = { error: null }; }
  static getDerivedStateFromError(error: any) { return { error }; }
  render() {
    if (this.state.error) return <div>Error en UI: {String(this.state.error)}</div>;
    return this.props.children;
  }
}