import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

import useEscapeKeyDown from '../../hooks/useEscapeKeyDown';

interface ModalProps {
  opened: boolean;
  onCloseClick: () => void;
}

function Modal({
  children,
  opened,
  onCloseClick,
}: PropsWithChildren<ModalProps>) {
  const handleClick = (event: React.MouseEvent<HTMLDivElement>) => {
    if (event.target === event.currentTarget) {
      onCloseClick();
    }
  };

  useEscapeKeyDown(onCloseClick, opened);

  return (
    opened && (
      <StyledOverlay onClick={handleClick}>
        <StyledContent>{children}</StyledContent>
      </StyledOverlay>
    )
  );
}

export default Modal;

const StyledOverlay = styled.div`
  position: fixed;
  top: 0;
  left: 0;

  width: 100%;
  height: 100%;

  background-color: rgb(0 0 0 / 50%);
`;

const StyledContent = styled.div`
  position: absolute;
  top: 50%;
  left: 50%;

  padding: 2.2rem;

  background-color: white;
  word-break: break-all;
  transform: translate(-50%, -50%);
  border-radius: 0.5rem;
  box-shadow: rgb(0 0 0 / 10%) 0 0.4rem 1.2rem;
`;
