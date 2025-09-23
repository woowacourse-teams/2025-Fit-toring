import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { PAGE_URL } from '../../common/constants/url';

import FitnessQuestionFlow from './components/FitnessQuestionFlow/FitnessQuestionFlow';
import Footer from './components/Footer/Footer';
import Introduce from './components/Introduce/Introduce';
import Slogan from './components/Slogan/Slogan';
import UserLevelGuide from './components/UserLevelGuide/UserLevelGuide';

function Landing() {
  const navigate = useNavigate();

  const handleStartButtonClick = () => {
    sessionStorage.setItem('hasVisited', 'true');
    navigate(PAGE_URL.HOME);
  };

  return (
    <div>
      <Slogan />
      <FitnessQuestionFlow />
      <Introduce />
      <UserLevelGuide />
      <S_ButtonSection>
        <S_Button onClick={handleStartButtonClick}>시작하기</S_Button>
      </S_ButtonSection>
      <Footer />
    </div>
  );
}

export default Landing;

const S_ButtonSection = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  padding-top: 5rem;
  padding-bottom: 10rem;
`;

const S_Button = styled.button`
  width: 13rem;
  padding: 1.5rem 2rem;
  border: 2px solid #e3e3e3;
  border: none;
  border-radius: 7px;

  background: ${({ theme }) => theme.SYSTEM.GRAY900};

  color: white;
  font-weight: 700;
  font-size: 1.7rem;
  text-align: center;
  cursor: pointer;
`;
