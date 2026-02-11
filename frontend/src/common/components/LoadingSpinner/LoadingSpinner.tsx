import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';
type Size = 'small' | 'medium' | 'large';

interface LoadingSpinnerProps {
  size?: Size;
}

const SIZE_MAP: Record<Size, string> = {
  small: '1.2rem',
  medium: '2.4rem',
  large: '3.6rem',
} as const;

const LoadingSpinner = ({ size = 'medium' }: LoadingSpinnerProps) => {
  return (
    <SpinnerContainer>
      <SpinnerItem size={SIZE_MAP[size]} />
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

const SpinnerItem = styled.div<{ size: string }>`
  width: ${({ size }) => size};
  height: ${({ size }) => size};
  border: 4px solid rgb(0 0 0 / 10%);
  border-radius: 50%;

  animation: ${spin} 1s ease infinite;
  border-left-color: #000;
`;
