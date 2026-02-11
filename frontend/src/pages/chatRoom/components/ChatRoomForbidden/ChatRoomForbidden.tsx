import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';

function ChatRoomForbidden() {
  const navigate = useNavigate();

  const handleGoHomeClick = () => {
    navigate(PAGE_URL.HOME, { replace: true });
  };

  return (
    <S_Container role="alert" aria-live="polite">
      <S_Card>
        <S_Icon aria-hidden="true">🔒</S_Icon>

        <S_Title>채팅방 접근 권한이 없습니다</S_Title>
        <S_Description>
          이 채팅방은 참여자만 볼 수 있어요. 초대 링크가 만료되었거나 권한이
          없을 수 있습니다.
        </S_Description>

        <S_ButtonRow>
          <S_PrimaryButton type="button" onClick={handleGoHomeClick}>
            홈으로 가기
          </S_PrimaryButton>
        </S_ButtonRow>
      </S_Card>
    </S_Container>
  );
}

export default ChatRoomForbidden;

const S_Container = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;

  min-height: 100vh;
  padding: 2.4rem;
`;

const S_Card = styled.div`
  width: 100%;
  max-width: 420px;
  padding: 28px 22px;
  border: 1px solid #e8e8e8;
  border-radius: 16px;
  box-shadow: 0 8px 24px rgb(0 0 0 / 6%);

  background: #fff;

  text-align: center;
`;

const S_Icon = styled.div`
  margin-bottom: 1.4rem;

  font-size: 40px;
  line-height: 1;
`;

const S_Title = styled.h2`
  margin: 0 0 1rem;

  color: #111;
  font-weight: 700;
  font-size: 18px;
`;

const S_Description = styled.p`
  margin: 0;

  color: #666;
  font-size: 14px;
  line-height: 1.5;
`;

const S_ButtonRow = styled.div`
  display: flex;
  justify-content: center;

  margin-top: 1.8rem;
`;

const S_PrimaryButton = styled.button`
  min-width: 14rem;
  padding: 1.2rem 1.4rem;
  border: none;
  border-radius: 12px;

  background: #111;

  color: #fff;
  font-weight: 600;
  font-size: 14px;
  cursor: pointer;

  &:hover {
    opacity: 0.92;
  }

  &:active {
    transform: translateY(1px);
  }

  &:focus-visible {
    outline: 3px solid rgb(17 17 17 / 25%);
    outline-offset: 2px;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;
