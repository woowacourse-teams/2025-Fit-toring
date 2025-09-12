import styled from '@emotion/styled';

import mock from '../../../../common/assets/images/mock.png';

function Introduce() {
  return (
    <StyledContainer>
      <StyledTextWrapper>
        <StyledTitle>
          온라인 운동 멘토링
          <br />
          중개 플랫폼
        </StyledTitle>
        <StyledTexts>
          <StyledText>
            핏토링은 운동 숙련자들이
            <br />
            자신의 경험과 노하우를 공유합니다.
          </StyledText>
          <StyledText>
            운동 초보자들이 합리적인 비용으로
            <br />
            1회성 멘토링을 받을 수 있습니다.
          </StyledText>
          <StyledText>
            원하는 멘토에게 신청하여 카카오톡
            <br />
            오픈 채팅을 통해 상담을 받을 수 있어요
          </StyledText>
        </StyledTexts>
      </StyledTextWrapper>
      <div>
        <StyledImg src={mock} alt="목업" />
      </div>
    </StyledContainer>
  );
}

export default Introduce;

const StyledContainer = styled.div`
  line-height: normal;
  padding: 3rem;
  display: flex;
  flex-direction: column;
  gap: 2rem;
`;

const StyledTitle = styled.p`
  font-size: 2.5rem;
  font-weight: bold;
`;

const StyledTextWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 3rem;
`;

const StyledTexts = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.3rem;
`;

const StyledText = styled.p`
  font-size: 1.6rem;
`;

const StyledImg = styled.img`
  width: 289px;
  aspect-ratio: 484 / 947;
  float: right;
`;
