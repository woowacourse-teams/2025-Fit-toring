import styled from '@emotion/styled';

import Button from '../../../../common/components/Button/Button';
import Modal from '../../../../common/components/Modal/Modal';

interface CommunityPostDeleteModalProps {
  opened: boolean;
  onCloseClick: () => void;
  onConfirmClick: () => void;
}

function CommunityPostDeleteModal({
  opened,
  onCloseClick,
  onConfirmClick,
}: CommunityPostDeleteModalProps) {
  return (
    <Modal opened={opened} onCloseClick={onCloseClick}>
      <S_Container>
        <S_Title>게시글을 삭제하시겠습니까?</S_Title>
        <S_Description>삭제한 게시글은 다시 복구할 수 없습니다.</S_Description>
        <S_ButtonWrapper>
          <S_ActionButton
            type="button"
            variant="secondary"
            onClick={onCloseClick}
          >
            아니오
          </S_ActionButton>
          <S_ActionButton type="button" onClick={onConfirmClick}>
            예
          </S_ActionButton>
        </S_ButtonWrapper>
      </S_Container>
    </Modal>
  );
}

export default CommunityPostDeleteModal;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;
`;

const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_SB}
`;

const S_Description = styled.p`
  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  justify-content: flex-end;
  gap: 0.8rem;
`;

const S_ActionButton = styled(Button)`
  min-width: 7.2rem;

  ${({ theme }) => theme.TYPOGRAPHY.C3_R}
`;
