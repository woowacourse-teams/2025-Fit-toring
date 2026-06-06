import styled from '@emotion/styled';

import closeIcon from '../../assets/images/closeBlack.svg';
import fittoringIconWithBg from '../../assets/images/fittoringIconWithBg.png';
import shareIcon from '../../assets/images/shareIcon.svg';
import Modal from '../Modal/Modal';

interface IOSInstallGuideModalProps {
  opened: boolean;
  onCloseClick: () => void;
  onLaterClick?: () => void;
}

function IOSInstallGuideModal({
  opened,
  onCloseClick,
  onLaterClick,
}: IOSInstallGuideModalProps) {
  const handleLaterClick = onLaterClick ?? onCloseClick;

  return (
    <Modal opened={opened} onCloseClick={onCloseClick}>
      <S_Container>
        <S_CloseButton type="button" onClick={onCloseClick} aria-label="닫기">
          <S_CloseIcon src={closeIcon} alt="" aria-hidden="true" />
        </S_CloseButton>

        <S_Header>
          <S_IconBox aria-hidden="true">
            <S_AppIcon src={fittoringIconWithBg} alt="" aria-hidden="true" />
          </S_IconBox>

          <S_Title>홈 화면에 핏토링 추가</S_Title>
          <S_Description>
            채팅과 예약 소식을
            <br />더 빠르게 확인해보세요.
          </S_Description>
        </S_Header>

        <S_StepList>
          <S_StepCard>
            <S_StepBadge>1</S_StepBadge>
            <S_StepBody>
              <S_StepTitleRow>
                <S_StepTitle>브라우저</S_StepTitle>
                <S_StepIcon src={shareIcon} alt="" aria-hidden="true" />
                <S_StepTitle>공유 버튼 탭</S_StepTitle>
              </S_StepTitleRow>
              <S_StepDescription>
                Safari 하단 / Chrome 상단 바 공유 아이콘을 눌러주세요.
              </S_StepDescription>
            </S_StepBody>
          </S_StepCard>

          <S_StepCard>
            <S_StepBadge>2</S_StepBadge>
            <S_StepBody>
              <S_StepTitle>&quot;홈 화면에 추가&quot; 선택</S_StepTitle>
            </S_StepBody>
          </S_StepCard>

          <S_StepCard>
            <S_StepBadge>3</S_StepBadge>
            <S_StepBody>
              <S_StepTitle>우측 상단 &quot;추가&quot; 탭</S_StepTitle>
              <S_StepDescription>
                홈 화면에서 앱 아이콘을 바로 실행할 수 있어요.
              </S_StepDescription>
            </S_StepBody>
          </S_StepCard>
        </S_StepList>
        <S_LaterButton type="button" onClick={handleLaterClick}>
          다음에 할래요
        </S_LaterButton>
      </S_Container>
    </Modal>
  );
}

export default IOSInstallGuideModal;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;
  position: relative;

  box-sizing: border-box;

  max-height: min(82vh, 72rem);
  overflow-y: auto;

  padding: 0.8rem 0.2rem 0;
`;

const S_CloseButton = styled.button`
  display: inline-flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 0;
  right: 0;

  padding: 0.4rem;
  border: none;

  background: transparent;
  cursor: pointer;
`;

const S_CloseIcon = styled.img`
  width: 1.5rem;
  height: 1.5rem;
`;

const S_Header = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.4rem;

  padding-top: 1.6rem;

  text-align: center;
`;

const S_IconBox = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  width: 8rem;
  height: 8rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY100};
  border-radius: 2rem;
`;

const S_AppIcon = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H3_B}
  text-align: center;
`;

const S_Description = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
  line-height: 1.55;
  text-align: center;
`;

const S_StepList = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.4rem;

  margin-top: 0.6rem;
`;

const S_StepCard = styled.div`
  display: flex;
  align-items: flex-start;
  gap: 1.2rem;

  padding: 1.6rem;
  border-radius: 1.8rem;

  background-color: ${({ theme }) => theme.BG.LIGHT};
`;

const S_StepBadge = styled.div`
  display: flex;
  flex-shrink: 0;
  align-items: center;
  justify-content: center;

  width: 2.8rem;
  height: 2.8rem;
  border-radius: 999px;

  background-color: ${({ theme }) => theme.FONT.B01};

  color: ${({ theme }) => theme.FONT.W01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_B}
`;

const S_StepBody = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 0.8rem;

  min-width: 0;
`;

const S_StepTitle = styled.h3`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_B}
  line-height: 1.45;
`;

const S_StepTitleRow = styled.div`
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  gap: 0.4rem;
`;

const S_StepIcon = styled.img`
  width: 1.8rem;
  height: 1.8rem;
`;

const S_StepDescription = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
  line-height: 1.55;
`;

const S_LaterButton = styled.button`
  align-self: center;

  border: none;

  background: transparent;

  color: ${({ theme }) => theme.FONT.G01};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
  cursor: pointer;
  text-decoration: underline;
  text-underline-offset: 0.3rem;
`;
