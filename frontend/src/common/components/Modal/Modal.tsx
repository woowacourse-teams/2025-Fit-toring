import type { PropsWithChildren } from 'react';

import styled from '@emotion/styled';

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
  width: 34rem;
  height: 30rem;
  padding: 2.2rem;
  word-break: break-all;

  background-color: white;
  transform: translate(-50%, -50%);
  border-radius: 0.5rem;
  box-shadow: rgb(0 0 0 / 10%) 0 0.4rem 1.2rem;
`;
