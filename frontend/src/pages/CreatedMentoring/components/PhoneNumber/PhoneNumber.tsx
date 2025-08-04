import styled from '@emotion/styled';

interface PhoneNumberProps {
  status: '승인 대기' | '승인됨' | '완료됨' | '거절됨';
  phoneNumber: string | null;
}

function PhoneNumber({ status, phoneNumber }: PhoneNumberProps) {
  const canShowPhoneNumber =
    (status === '승인됨' || status === '완료됨') && phoneNumber;

  return canShowPhoneNumber ? (
    <StyledContainer>연락처: {phoneNumber}</StyledContainer>
  ) : null;
}

export default PhoneNumber;

const StyledContainer = styled.p`
  width: fit-content;

  background-color: ${({ theme }) => theme.BG.YELLOW};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
