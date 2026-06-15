import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';

interface SkeletonProps {
  className?: string;
}

function Skeleton({ className }: SkeletonProps) {
  return <S_Skeleton className={className} aria-hidden="true" />;
}

export default Skeleton;

const shimmer = keyframes`
  0% {
    background-position: -200% 0;
  }

  100% {
    background-position: 200% 0;
  }
`;

const S_Skeleton = styled.div`
  border-radius: 6px;

  background: linear-gradient(
    90deg,
    ${({ theme }) => theme.SYSTEM.GRAY100} 0%,
    ${({ theme }) => theme.SYSTEM.GRAY50} 50%,
    ${({ theme }) => theme.SYSTEM.GRAY100} 100%
  );
  background-size: 400% 100%;

  animation: ${shimmer} 1.2s ease-in-out infinite;
`;
