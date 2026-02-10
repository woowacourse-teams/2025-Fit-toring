import styled from '@emotion/styled';
import { Outlet } from 'react-router-dom';

function MobileLayout() {
  return (
    <S_Container>
      <S_Contents>
        <Outlet />
      </S_Contents>
    </S_Container>
  );
}

export default MobileLayout;

const S_Container = styled.main`
  display: flex;
  justify-content: center;

  width: 100%;
  min-height: 100dvh;
`;

const S_Contents = styled.section`
  width: 48rem;
  height: 100%;
  min-height: 100dvh;
  padding-bottom: 72px;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};

  background-color: ${({ theme }) => theme.BG.WHITE};

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;
