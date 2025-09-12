import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { PAGE_URL } from '../../../common/constants/url';

function Slogan() {
  const TAGS = ['다이어트', '홈트레이닝', '체형교정', '벌크업', '재활운동'];

  const navigate = useNavigate();

  const handleStartButtonClick = () => {
    navigate(PAGE_URL.HOME);
  };

  return (
    <StyledContainer>
      <StyledSloganSection>
        <StyledSloganWrapper>
          <StyledSloganText>일대일 온라인 운동 상담 플랫폼</StyledSloganText>
          <StyledTextWrapper>
            <StyledNameText highlight>Fit</StyledNameText>
            <StyledNameText>toring</StyledNameText>
          </StyledTextWrapper>
        </StyledSloganWrapper>
        <StyledTags>
          {TAGS.map((tag) => (
            <StyledTag>#{tag}</StyledTag>
          ))}
        </StyledTags>
        <StyledButton onClick={handleStartButtonClick}>시작하기</StyledButton>
      </StyledSloganSection>
    </StyledContainer>
  );
}

export default Slogan;

const StyledContainer = styled.div`
  height: 100dvh;
  display: flex;
  justify-content: center;
  align-items: center;
`;

const StyledSloganSection = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6rem;
`;

const StyledSloganWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
`;

const StyledSloganText = styled.p`
  color: #000;
  font-size: 6rem;
  font-weight: 700;
`;

const StyledTextWrapper = styled.div`
  display: flex;
`;

const StyledNameText = styled.span<{ highlight?: boolean }>`
  font-size: 11.2rem;
  font-weight: 700;
  color: ${({ theme, highlight }) =>
    highlight ? theme.SYSTEM.MAIN500 : '#000}'};
`;

const StyledTags = styled.div`
  display: flex;
  gap: 2.2rem;
`;

const StyledTag = styled.span`
  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  font-size: 3rem;
  font-weight: 500;
`;

const StyledButton = styled.button`
  width: 19rem;
  border-radius: 15px;
  padding: 2rem 4rem;
  border: 2px solid #e3e3e3;
  background: #fff;
  color: #000;
  text-align: center;
  font-size: 2.5rem;
  font-weight: 700;
  cursor: pointer;
`;
