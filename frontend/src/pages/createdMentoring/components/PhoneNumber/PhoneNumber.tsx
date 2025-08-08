import styled from '@emotion/styled';

import {
  StatusTypeEnum,
  type StatusType,
} from '../../../../common/types/statusType';

interface PhoneNumberProps {
  status: StatusType;
  phoneNumber: string | null;
}

function PhoneNumber({ status, phoneNumber }: PhoneNumberProps) {
  const canShowPhoneNumber =
    (status === StatusTypeEnum.approved ||
      status === StatusTypeEnum.completed) &&
    phoneNumber;

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
