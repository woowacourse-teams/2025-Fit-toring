import styled from '@emotion/styled';

import goIcon from '../../../../common/assets/images/goIcon.svg';

interface SpecialtyFilterModalButtonProps {
  handleOpenModal: () => void;
}

function SpecialtyFilterModalButton({
  handleOpenModal,
}: SpecialtyFilterModalButtonProps) {
  return (
    <S_Button onClick={handleOpenModal} type="button">
      <S_GoIcon src={goIcon} alt="카테고리 열기 아이콘" />
      <S_Text>카테고리</S_Text>
    </S_Button>
  );
}

export default SpecialtyFilterModalButton;

const S_Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;

  width: 9.4rem;
  height: 3.4rem;
  padding: 1rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 6.75px;

  background: ${({ theme }) => theme.BG.WHITE};
  cursor: pointer;
`;

const S_Text = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
  color: ${({ theme }) => theme.SYSTEM.GRAY600};
`;

const S_GoIcon = styled.img`
  width: 1.4rem;
  aspect-ratio: 1 / 1;
`;
