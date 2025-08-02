import styled from '@emotion/styled';

import MyProfile from '../myPage/components/MyProfile/MyProfile';

import MyPageHeader from './components/MyPageHeader/MyPageHeader';

function MyPage() {
  return (
    <StyledContainer>
      <MyPageHeader />
      <MyProfile />
    </StyledContainer>
  );
}

export default MyPage;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;

  height: 100%;
`;
