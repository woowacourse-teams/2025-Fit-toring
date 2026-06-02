import styled from '@emotion/styled';
import { useNavigate, useNavigationType } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';
import Header from '../../../../common/components/Header/Header';
import { PAGE_URL } from '../../../../common/constants/url';

function DetailHeader() {
  const navigate = useNavigate();
  const navigationType = useNavigationType();

  const handleMoveBack = () => {
    if (navigationType === 'POP') {
      navigate(PAGE_URL.HOME, { replace: true });
    } else {
      navigate(-1);
    }
  };
  return (
    <Header overlay>
      <S_HeaderWrapper>
        <S_BackButton onClick={handleMoveBack}>
          <S_Img src={backIcon} alt="뒤로가기 버튼" />
        </S_BackButton>
      </S_HeaderWrapper>
    </Header>
  );
}

export default DetailHeader;

const S_HeaderWrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
`;

const S_BackButton = styled.button`
  position: absolute;

  width: 4.4rem;
  height: 4.4rem;
  margin-left: 1rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_Img = styled.img`
  width: 3.8rem;

  filter: brightness(0) invert(1) drop-shadow(0 0.2rem 0.45rem rgb(0 0 0 / 70%));
`;
