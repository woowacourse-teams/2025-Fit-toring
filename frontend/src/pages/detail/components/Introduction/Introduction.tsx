import styled from '@emotion/styled';

function Introduction() {
  return (
    <StyledContainer>
      <StyledH4>멘토 소개</StyledH4>
      {`❌ 헬스장 갑질 논란\n✅ PT 결제 후 트레이너 태도가 변했어요.\n❗ 강압적인 트레이너의 수업 방식이 불편해요.\n\n※ 이상탁 트레이너는 다릅니다.\n\n1️⃣ 회원 만족 서비스 제공\n회원님의 귀한 시간과 돈을 투자하여 ‘고용’하는 트레이너로서\n회원님의 운동 목적 달성과 서비스 만족을 최우선으로 생각합니다.\n\n✓ 운동 관리는 물론, 신체의 움직임 원리를 기반으로\n수업하기에 혼자서도 운동을 잘할 수 있게 만들어 드립니다.\n\n✓ 일률적인 수업이 아닌 회원님의 운동 경력, 부상 경험\n개인 성향 등 회원님보다 더 회원님 몸에 대해 고민하고\n연구하여 최적의 수업을 제공합니다.\n\n2️⃣ 전문적인 수업 스펙트럼\n기계 체조, 육상 엘리트 선수를 준비하며 다양한 트레이닝을 경험하였고,\n움직임 원리에 기반한 트레이닝으로 응용하고 있습니다.\n\n또한, 발목 인대와 슬관절 부상, 허리 디스크까지 수많은 부상을 직접 겪었고 극복했기에\n부상을 방지할 수 있는 운동, 재활 운동에 대한 자신이 있습니다.`}
    </StyledContainer>
  );
}

export default Introduction;

const StyledContainer = styled.p`
  width: 100%;
  padding: 0 1rem;

  white-space: pre-line;
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const StyledH4 = styled.h4`
  display: flex;
  justify-content: center;
  margin-bottom: 1.7rem;

  ${({ theme }) => theme.TYPOGRAPHY.H4_R}
  color: ${({ theme }) => theme.FONT.B01};
`;
