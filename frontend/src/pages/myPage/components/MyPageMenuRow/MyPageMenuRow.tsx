import styled from '@emotion/styled';

import chevronRightIcon from '../../../../common/assets/images/mypage-chevron-right.svg';

interface MyPageMenuRowProps {
  iconSrc: string;
  label: string;
  onClick: () => void;
}

function MyPageMenuRow({ iconSrc, label, onClick }: MyPageMenuRowProps) {
  return (
    <S_Button onClick={onClick} type="button">
      <S_Content>
        <S_LeadingIcon src={iconSrc} alt="" aria-hidden="true" />
        <S_Label>{label}</S_Label>
      </S_Content>
      <S_ChevronIcon src={chevronRightIcon} alt="" aria-hidden="true" />
    </S_Button>
  );
}

export default MyPageMenuRow;

const S_Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.6rem;

  width: 100%;
  padding: 0;
  border: none;

  background: transparent;
  appearance: none;
  cursor: pointer;

  font: inherit;
  text-align: left;
`;

const S_Content = styled.span`
  display: flex;
  align-items: center;
  gap: 1.5rem;

  min-width: 0;
`;

const S_LeadingIcon = styled.img`
  flex-shrink: 0;

  width: 2rem;
  height: 2rem;
`;

const S_Label = styled.span`
  overflow: hidden;

  color: ${({ theme }) => theme.FONT.B01};
  text-overflow: ellipsis;

  white-space: nowrap;
  ${({ theme }) => theme.TYPOGRAPHY.H4_R}
`;

const S_ChevronIcon = styled.img`
  flex-shrink: 0;

  width: 2.4rem;
  height: 2.4rem;
`;
