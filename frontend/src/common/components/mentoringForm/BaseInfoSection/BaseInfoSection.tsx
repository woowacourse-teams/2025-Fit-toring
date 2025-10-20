import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { getUserInfoSummary } from '../../../apis/getUserInfoSummary';
import { captureSentryError } from '../../../utils/captureSentryError';
import FormField from '../../FormField/FormField';
import Input from '../../Input/Input';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

import type { mentoringCreateFormData } from '../../../types/mentoringCreateFormData';
import type { UserInfoResponse } from '../../../types/userInfoResponse';

interface BaseInfoSectionProps {
  priceErrorMessage: string;
  onBaseInfoChange: (
    newData: Partial<Pick<mentoringCreateFormData, 'price'>>,
  ) => void;
  price: number;
}

function BaseInfoSection({
  onBaseInfoChange,
  priceErrorMessage,
  price,
}: BaseInfoSectionProps) {
  const [userInfo, setUserInfo] = useState<UserInfoResponse>({
    name: '',
    phoneNumber: '',
  });

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const response = await getUserInfoSummary();
        setUserInfo(response);
      } catch (error) {
        console.error('사용자 정보 조회 실패:', error);
        captureSentryError({
          error,
          level: 'warning',
          feature: 'mentoring',
          step: 'base-info-fetch',
        });
      }
    };

    fetchUserInfo();
  }, []);

  const handlePriceChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    onBaseInfoChange({ price: Number(e.target.value) });
  };

  return (
    <section>
      <TitleSeparator>기본 정보</TitleSeparator>
      <S_FormFieldWrapper>
        <FormField label="이름 *">
          <Input value={userInfo.name} id="name" disabled />
        </FormField>
        <FormField label="전화번호 *">
          <Input value={userInfo.phoneNumber} id="phone" disabled />
        </FormField>

        <FormField label="15분 상담료 (원) *" errorMessage={priceErrorMessage}>
          <Input
            placeholder="5000"
            id="price"
            required
            onChange={handlePriceChange}
            errored={priceErrorMessage !== ''}
            value={price}
          />
        </FormField>
      </S_FormFieldWrapper>
    </section>
  );
}

export default BaseInfoSection;

const S_FormFieldWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;

  & input {
    &:hover {
      border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
    }

    &:focus {
      border-color: ${({ theme }) => theme.SYSTEM.MAIN500};

      box-shadow: 0 0 0 1px ${({ theme }) => theme.SYSTEM.MAIN500};
    }
  }
`;
