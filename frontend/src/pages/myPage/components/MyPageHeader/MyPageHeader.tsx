import styled from '@emotion/styled';

import Header from '../../../../common/components/Header/Header';

function MyPageHeader() {
  return (
    <Header>
      <StyledWrapper></StyledWrapper>
    </Header>
  );
}

export default MyPageHeader;

const StyledWrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
`;
