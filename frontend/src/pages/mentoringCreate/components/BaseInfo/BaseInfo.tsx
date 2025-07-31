import styled from '@emotion/styled';

import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

function BaseInfo() {
  return (
    <>
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
    </>
  );
}

export default BaseInfo;

const StyledFormFieldWrapper = styled.section`
  display: flex;
  flex-direction: column;
  gap: 2rem;
`;
