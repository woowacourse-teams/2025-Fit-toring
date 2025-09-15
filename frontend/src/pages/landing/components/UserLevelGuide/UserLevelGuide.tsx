import React from 'react';

import styled from '@emotion/styled';

import beginner from '../../../../common/assets/images/beginner.png';
import expert from '../../../../common/assets/images/expert.png';

function UserLevelGuide() {
  return (
    <StyledContainer>
      <StyledUserWrapper>
        <StyledImg src={beginner} alt="초보자 이미지" />
        <StyledName>운동 초보자</StyledName>
        <StyledDescription>
          소액으로 1회성 <br />
          온라인 운동 멘토링
        </StyledDescription>
      </StyledUserWrapper>
      <StyledUserWrapper>
        <StyledImg src={expert} alt="숙련자 이미지" />
        <StyledName>운동 숙련자</StyledName>
        <StyledDescription>
          전문성과 경험을 공유해 <br />
          추가 수익 창출
        </StyledDescription>
      </StyledUserWrapper>
    </StyledContainer>
  );
}

export default UserLevelGuide;

const StyledContainer = styled.div`
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 7rem 3rem;
  line-height: normal;
  gap: 3.5rem;
`;

const StyledUserWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
  align-items: center;
`;

const StyledImg = styled.img`
  width: 65px;
  aspect-ratio: 306 / 400;
`;

const StyledName = styled.p`
  font-size: 1.7rem;
  font-weight: bold;
`;

const StyledDescription = styled.p`
  font-size: 1.5rem;
  text-align: center;
`;
