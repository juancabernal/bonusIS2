import React from 'react';

type VerifyCodeModalProps = {
  open?: boolean;
  onClose?: () => void;
};

export default function VerifyCodeModal({ open = false, onClose }: VerifyCodeModalProps) {
  if (!open) return null;

  return (
    <div role="dialog" aria-modal="true" style={{ padding: 16, border: '1px solid #ccc', background: '#fff' }}>
      <h2>Verificar código</h2>
      <p>Introduce el código que recibiste.</p>
      <button onClick={onClose}>Cerrar</button>
    </div>
  );
}