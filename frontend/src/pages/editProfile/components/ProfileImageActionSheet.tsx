import { useRef } from 'react';

import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';

import Portal from '../../../common/components/Portal/Portal';
import useEscapeKeyDown from '../../../common/hooks/useEscapeKeyDown';
import { useFocusTrap } from '../../../common/hooks/useFocusTrap';

interface ProfileImageActionSheetProps {
  opened: boolean;
  showDeleteButton: boolean;
  onAlbumSelectClick: () => void;
  onDeleteClick: () => void;
  onCloseClick: () => void;
}

const fadeIn = keyframes`
  from {
    opacity: 0;
  }

  to {
    opacity: 1;
  }
`;

const slideUp = keyframes`
  from {
    opacity: 0.4;
    filter: blur(0.2rem);
    transform: translateY(110%);
  }

  to {
    opacity: 1;
    filter: blur(0);
    transform: translateY(0);
  }
`;

function ProfileImageActionSheet({
  opened,
  showDeleteButton,
  onAlbumSelectClick,
  onDeleteClick,
  onCloseClick,
}: ProfileImageActionSheetProps) {
  const sheetRef = useRef<HTMLDivElement>(null);

  useEscapeKeyDown(onCloseClick, opened);
  useFocusTrap(sheetRef, opened);

  const handleOverlayClick = (e: React.MouseEvent<HTMLDivElement>) => {
    if (e.target === e.currentTarget) {
      onCloseClick();
    }
  };

  if (!opened) {
    return null;
  }

  return (
    <Portal>
      <S_Overlay onClick={handleOverlayClick}>
        <S_Sheet ref={sheetRef} role="dialog" aria-modal="true">
          <S_Handle aria-hidden="true" />
          <S_ActionGroup>
            <S_ActionButton type="button" onClick={onAlbumSelectClick}>
              앨범에서 선택
            </S_ActionButton>
            {showDeleteButton && (
              <S_DeleteButton type="button" onClick={onDeleteClick}>
                프로필 사진 삭제
              </S_DeleteButton>
            )}
          </S_ActionGroup>
          <S_ActionGroup>
            <S_ActionButton type="button" onClick={onCloseClick}>
              닫기
            </S_ActionButton>
          </S_ActionGroup>
        </S_Sheet>
      </S_Overlay>
    </Portal>
  );
}

export default ProfileImageActionSheet;

const S_Overlay = styled.div`
  display: flex;
  align-items: flex-end;
  justify-content: center;
  position: fixed;
  top: 0;
  left: 0;
  z-index: 1000;

  width: 100%;
  height: 100dvh;

  background-color: rgb(0 0 0 / 35%);

  animation: ${fadeIn} 280ms ease-out;
`;

const S_Sheet = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.2rem;

  width: 100%;
  max-width: 48rem;
  padding: 1.2rem 2.4rem calc(2.6rem + env(safe-area-inset-bottom));
  border-radius: 2.8rem 2.8rem 0 0;

  box-sizing: border-box;

  background-color: ${({ theme }) => theme.BG.WHITE};

  animation: ${slideUp} 520ms cubic-bezier(0.16, 1, 0.3, 1);
`;

const S_Handle = styled.div`
  width: 4.8rem;
  height: 0.6rem;
  border-radius: 999px;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY200};
`;

const S_ActionGroup = styled.div`
  overflow: hidden;

  width: 100%;
  border-radius: 1.8rem;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_ActionButton = styled.button`
  width: 100%;
  height: 4.8rem;
  border: none;

  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B01};

  ${({ theme }) => theme.TYPOGRAPHY.B2_SB};
  cursor: pointer;
`;

const S_DeleteButton = styled(S_ActionButton)`
  border-top: 1px solid ${({ theme }) => theme.SYSTEM.GRAY100};

  color: ${({ theme }) => theme.BG.RED};
`;
