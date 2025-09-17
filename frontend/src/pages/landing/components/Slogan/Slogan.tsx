import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';

function Slogan() {
  const TAGS = ['다이어트', '홈트레이닝', '체형교정', '벌크업', '재활운동'];

  const navigate = useNavigate();

  const handleStartButtonClick = () => {
    sessionStorage.setItem('hasVisited', 'true');
    navigate(PAGE_URL.HOME);
  };

  return (
    <S_Container>
      <S_SloganSection>
        <S_SloganWrapper>
          <S_SloganText>일대일 온라인 운동 상담 플랫폼</S_SloganText>
          <S_TextWrapper>
            <S_NameText highlight>Fit</S_NameText>
            <S_NameText>toring</S_NameText>
          </S_TextWrapper>
        </S_SloganWrapper>
        <S_Tags>
          {TAGS.map((tag) => (
            <S_Tag key={tag}>#{tag}</S_Tag>
          ))}
        </S_Tags>
        <S_Button onClick={handleStartButtonClick}>시작하기</S_Button>
      </S_SloganSection>
    </S_Container>
  );
}

export default Slogan;

const S_Container = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  height: 50rem;
  padding: 0 3rem;
`;

const S_SloganSection = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 3rem;
`;

const S_SloganWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
`;

const S_SloganText = styled.p`
  color: #000;
  font-weight: 700;
  font-size: 2.5rem;
`;

const S_TextWrapper = styled.div`
  display: flex;
`;

const S_NameText = styled.span<{ highlight?: boolean }>`
  color: ${({ theme, highlight }) =>
    highlight ? theme.SYSTEM.MAIN500 : '#000'};
  font-weight: 700;
  font-size: 5rem;
`;

const S_Tags = styled.div`
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 1rem;

  width: 24rem;
`;

const S_Tag = styled.span`
  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  font-weight: 500;
  font-size: 1.5rem;
`;

const S_Button = styled.button`
  width: 10rem;
  padding: 1rem 2rem;
  border: 2px solid #e3e3e3;
  border: none;
  border-radius: 7px;

  background: ${({ theme }) => theme.SYSTEM.GRAY900};

  color: white;
  font-weight: 700;
  font-size: 1.5rem;
  text-align: center;
  cursor: pointer;
`;
