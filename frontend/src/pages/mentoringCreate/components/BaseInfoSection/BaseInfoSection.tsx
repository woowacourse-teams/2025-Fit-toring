import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';
import { getUserInfo } from '../../apis/getUserInfo';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

import type { mentoringCreateFormData } from '../types/mentoringCreateFormData';
import type { UserInfoResponse } from '../types/userInfoResponse';

interface BaseInfoSectionProps {
  priceErrorMessage: string;
  onPriceChange: (newData: Pick<mentoringCreateFormData, 'price'>) => void;
}

function BaseInfoSection({
  onPriceChange,
  priceErrorMessage,
}: BaseInfoSectionProps) {
  const [userInfo, setUserInfo] = useState<UserInfoResponse>({
    name: '',
    phoneNumber: '',
  });

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await getUserInfo();
        setUserInfo(response);
      } catch (error) {
        console.error('사용자 정보 조회 실패:', error);
      }
    };

    fetchUserInfo();
  }, []);

  const handlePriceChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onPriceChange({ price: Number(e.target.value) });
  };

  return (
    <section>
      <TitleSeparator>기본 정보</TitleSeparator>
      <StyledFormFieldWrapper>
        <FormField label="이름 *">
          <Input value={userInfo.name} id="name" readOnly />
        </FormField>
        <FormField label="15분 상담료 (원) *" errorMessage={priceErrorMessage}>
          <Input
            placeholder="5000"
            id="price"
            required
            onChange={handlePriceChange}
            errored={priceErrorMessage !== ''}
          />
        </FormField>
        <FormField label="전화번호 *">
          <Input value={userInfo.phoneNumber} id="phone" readOnly />
        </FormField>
      </StyledFormFieldWrapper>
    </section>
  );
}

export default BaseInfoSection;

const StyledFormFieldWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;

  & > div > input {
    &:hover {
      border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
    }

    &:focus {
      border-color: ${({ theme }) => theme.SYSTEM.MAIN500};

      box-shadow: 0 0 0 1px ${({ theme }) => theme.SYSTEM.MAIN500};
    }
  }
`;
