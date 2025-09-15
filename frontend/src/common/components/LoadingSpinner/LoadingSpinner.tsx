import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';

const LoadingSpinner = () => {
  return (
    <SpinnerContainer>
      <SpinnerItem />
    </SpinnerContainer>
  );
};

export default LoadingSpinner;

const SpinnerContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 100%;
`;

const spin = keyframes`
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  `;

const SpinnerItem = styled.div`
  width: 3.6rem;
  height: 3.6rem;
  border: 4px solid rgb(0 0 0 / 10%);
  border-radius: 50%;

  animation: ${spin} 1s ease infinite;
  border-left-color: #000;
`;
