import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import writeIcon from '../../../../common/assets/images/writeIcon.svg';
import { PAGE_URL } from '../../../../common/constants/url';

interface WriteFloatingButtonProps {
  label: string;
}

function WriteButton({ label }: WriteFloatingButtonProps) {
  const navigate = useNavigate();

  const handleWriteButtonClick = () => {
    navigate(PAGE_URL.COMMUNITY_CREATE);
  };

  return (
    <S_Button type="button" onClick={handleWriteButtonClick}>
      <S_Icon aria-hidden="true" src={writeIcon} alt="" />
      <span>{label}</span>
    </S_Button>
  );
}

export default WriteButton;

const S_Button = styled.button`
  display: inline-flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.45rem;
  position: fixed;
  right: max(1.6rem, calc((100vw - 48rem) / 2 + 1.6rem));
  bottom: 9rem;
  z-index: 20;

  height: 4.8rem;
  min-width: 10.9rem;
  padding: 0 1.8rem;
  border: none;
  border-radius: 2.4rem;
  box-shadow: 0 0.3rem 0.8rem rgb(17 17 17 / 10%);

  background: ${({ theme }) => theme.SYSTEM.MAIN500};
  cursor: pointer;

  color: ${({ theme }) => theme.FONT.W01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_B};
  line-height: 1;
`;

const S_Icon = styled.img`
  width: 1.8rem;
  height: 1.8rem;
`;
