import styled from '@emotion/styled';

import MyPageHeader from './components/MyPageHeader/MyPageHeader';

function MyPage() {
  return (
    <StyledContainer>
      <MyPageHeader />
    </StyledContainer>
  );
}

export default MyPage;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;

  height: 100%;
`;
