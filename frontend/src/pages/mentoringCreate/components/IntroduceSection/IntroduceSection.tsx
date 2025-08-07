import styled from '@emotion/styled';

import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

import type { mentoringCreateFormData } from '../types/mentoringCreateFormData';

interface IntroduceSectionProps {
  onIntroduceChange: (
    newData: Partial<Pick<mentoringCreateFormData, 'introduction' | 'career'>>,
  ) => void;
}

function IntroduceSection({ onIntroduceChange }: IntroduceSectionProps) {
  return (
    <section>
      <TitleSeparator>소개 및 경력</TitleSeparator>
      <StyledFormFieldWrapper>
        <FormField label="한줄 소개">
          <Input
            placeholder="간단한 소개를 한 줄로 작성해주세요"
            id="introduce"
            onChange={(event) =>
              onIntroduceChange({ introduction: event.target.value })
            }
          />
        </FormField>
        <FormField label="경력">
          <Input
            placeholder="숫자만 입력해주세요."
            type="tel"
            id="career"
            onChange={(event) =>
              onIntroduceChange({ career: Number(event.target.value) })
            }
          />
        </FormField>
      </StyledFormFieldWrapper>
    </section>
  );
}

export default IntroduceSection;

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
