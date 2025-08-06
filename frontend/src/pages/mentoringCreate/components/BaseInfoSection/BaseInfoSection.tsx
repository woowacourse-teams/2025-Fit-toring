import styled from '@emotion/styled';

import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

function BaseInfoSection() {
  return (
    <section>
      <TitleSeparator>기본 정보</TitleSeparator>
      <StyledFormFieldWrapper>
        <FormField label="이름 *" htmlFor="name">
          <Input placeholder="홍길동" id="name" required />
        </FormField>
        <FormField label="15분 상담료 (원) *" htmlFor="price">
          <Input placeholder="5,000" id="price" required />
        </FormField>
        <FormField label="전화번호 *" htmlFor="phone">
          <Input placeholder="010-1234-5678" type="tel" id="phone" required />
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
