import styled from '@emotion/styled';

import FormField from '../../FormField/FormField';
import Input from '../../Input/Input';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

import type { mentoringCreateFormData } from '../../../types/mentoringCreateFormData';

interface IntroduceSectionProps {
  onIntroduceChange: (
    newData: Partial<Pick<mentoringCreateFormData, 'introduction' | 'career'>>,
  ) => void;
  introduceErrorMessage: string;
  careerErrorMessage: string;
  introduce: string;
  career: number;
}

function IntroduceSection({
  onIntroduceChange,
  introduceErrorMessage,
  careerErrorMessage,
  introduce,
  career,
}: IntroduceSectionProps) {
  return (
    <section>
      <TitleSeparator>소개 및 경력</TitleSeparator>
      <S_FormFieldWrapper>
        <FormField label="한줄 소개" errorMessage={introduceErrorMessage}>
          <Input
            placeholder="간단한 소개를 한 줄로 작성해주세요"
            id="introduce"
            onChange={(e) =>
              onIntroduceChange({ introduction: e.target.value })
            }
            value={introduce}
            errored={introduceErrorMessage !== ''}
          />
        </FormField>
        <FormField label="경력" errorMessage={careerErrorMessage}>
          <Input
            placeholder="숫자만 입력해주세요."
            type="tel"
            id="career"
            onChange={(e) =>
              onIntroduceChange({ career: Number(e.target.value) })
            }
            value={career}
            errored={careerErrorMessage !== ''}
          />
        </FormField>
      </S_FormFieldWrapper>
    </section>
  );
}

export default IntroduceSection;

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
