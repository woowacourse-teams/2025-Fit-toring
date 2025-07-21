import styled from '@emotion/styled';

import DetailHeader from './components/DetailHeader/DetailHeader';
import Profile from './components/Profile/Profile';
import Summary from './components/Summary/Summary';

function Detail() {
  return (
    <>
      <DetailHeader />
      <Profile />
      <Summary />
      <StyledH4>멘토 소개</StyledH4>
    </>
  );
}

export default Detail;

const StyledH4 = styled.h4`
  display: flex;
  justify-content: center;

  ${({ theme }) => theme.TYPOGRAPHY.H4_R}
  color: ${({ theme }) => theme.FONT.B01};
`;
