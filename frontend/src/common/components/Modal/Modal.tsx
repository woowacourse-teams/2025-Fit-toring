import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

import useEscapeKeyDown from '../../hooks/useEscapeKeyDown';

interface ModalProps {
  opened: boolean;
  onCloseClick: () => void;
  zIndex?: number;
}

function Modal({
  children,
  opened,
  onCloseClick,
  zIndex = 1000,
}: PropsWithChildren<ModalProps>) {
  const handleClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      onCloseClick();
    }
  };

  useEscapeKeyDown(onCloseClick, opened);

  return (
    opened && (
      <S_Overlay onClick={handleClick} zIndex={zIndex}>
        <S_Content>{children}</S_Content>
      </S_Overlay>
    )
  );
}

export default Modal;

const S_Overlay = styled.div<Pick<ModalProps, 'zIndex'>>`
  position: fixed;
  top: 0;
  left: 0;
  z-index: ${({ zIndex }) => zIndex};

  width: 100%;
  height: 100%;

  background-color: rgb(0 0 0 / 50%);
`;

const S_Content = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;

  width: 100%;
  max-width: 33rem;
  padding: 2.2rem;
  border-radius: 0.5rem;
  box-shadow: rgb(0 0 0 / 10%) 0 0.4rem 1.2rem;

  background-color: white;
  word-break: break-all;
  transform: translate(-50%, -50%);
`;
