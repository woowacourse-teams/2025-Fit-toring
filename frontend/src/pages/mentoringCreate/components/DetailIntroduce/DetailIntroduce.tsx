import styled from '@emotion/styled';

import FormField from '../../../../common/components/FormField/FormField';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

function DetailIntroduce() {
  return (
    <section>
      <TitleSeparator>상세 소개</TitleSeparator>
      <StyledFormFieldWrapper>
        <FormField label="상세 소개" htmlFor="content">
          <StyledTextarea
            placeholder="멘토링 경험, 전문성, 제공하는 서비스 등을 자세히 소개해주세요"
            id="content"
            required
          />
        </FormField>
      </StyledFormFieldWrapper>
    </section>
  );
}

export default DetailIntroduce;

const StyledFormFieldWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
`;

const StyledTextarea = styled.textarea`
  width: 100%;
  height: 25rem;
  padding: 2rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 12px;
  resize: none;

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R};

  &:hover {
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  &:focus {
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};

    outline: none;
    box-shadow: 0 0 0 1px ${({ theme }) => theme.SYSTEM.MAIN500};
  }
`;
