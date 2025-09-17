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
    (status === StatusTypeEnum.APPROVED ||
      status === StatusTypeEnum.COMPLETE) &&
    phoneNumber;

  return canShowPhoneNumber ? (
    <S_Container>연락처: {phoneNumber}</S_Container>
  ) : null;
}

export default PhoneNumber;

const S_Container = styled.p`
  width: fit-content;

  background-color: ${({ theme }) => theme.BG.YELLOW};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
