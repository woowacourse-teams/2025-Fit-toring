import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import backIcon from '../../../../common/assets/images/backIcon.svg';

interface ChatRoomHeader {
  name: string;
}

function ChatRoomHeader({ name }: ChatRoomHeader) {
  const navigate = useNavigate();

  const handleBackButtonClick = () => {
    navigate(-1);
  };

  return (
    <S_Container>
      <S_Wrapper>
        <S_BackButton onClick={handleBackButtonClick}>
          <S_BackIcon src={backIcon} alt="뒤로가기 아이콘" />
        </S_BackButton>
        <S_Title>{name}님과의 멘토링</S_Title>
      </S_Wrapper>
    </S_Container>
  );
}

export default ChatRoomHeader;

const S_Container = styled.header`
  width: 100%;
  height: 5.7rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Wrapper = styled.div`
  display: flex;
  align-items: center;

  height: 100%;
`;

const S_BackButton = styled.button`
  position: absolute;

  margin-left: 1rem;
  padding: 0;
  border: none;

  background-color: transparent;
  cursor: pointer;
`;

const S_Title = styled.h1`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;

const S_BackIcon = styled.img`
  width: 3.4rem;
`;
