import styled from '@emotion/styled';

import FormField from '../../FormField/FormField';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

import type { mentoringCreateFormData } from '../../../types/mentoringCreateFormData';

interface DetailIntroduceProps {
  onDetailIntroduceChange: (
    newData: Pick<mentoringCreateFormData, 'content'>,
  ) => void;
  detailIntroduce: string;
  detailErrorMessage: string;
}

function DetailIntroduce({
  onDetailIntroduceChange,
  detailIntroduce,
  detailErrorMessage,
}: DetailIntroduceProps) {
  const handleIntroduceChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    const { value } = e.target;

    if (value.length > 5000) {
      return;
    }

    onDetailIntroduceChange({ content: e.target.value });
  };
  return (
    <section>
      <TitleSeparator>상세 소개</TitleSeparator>
      <S_FormFieldWrapper>
        <FormField label="상세 소개 *" errorMessage={detailErrorMessage}>
          <S_TextareaWrapper>
            <S_Textarea
              placeholder="멘토링 경험, 전문성, 제공하는 서비스 등을 자세히 소개해주세요"
              id="content"
              onChange={handleIntroduceChange}
              value={detailIntroduce}
              required
            />
            <S_TextAreaCounter>
              {`(${detailIntroduce.length}/5000)`}
            </S_TextAreaCounter>
          </S_TextareaWrapper>
        </FormField>
      </S_FormFieldWrapper>
    </section>
  );
}

export default DetailIntroduce;

const S_FormFieldWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 2rem;
`;

const S_TextareaWrapper = styled.div`
  display: flex;
  flex-direction: column;

  width: 100%;
  height: 25rem;
  padding-bottom: 1rem;
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

const S_Textarea = styled.textarea`
  width: 100%;
  height: 100%;
  padding: 2rem;
  padding-bottom: 1rem;
  border: none;
  border-radius: 12px;
  resize: none;

  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R};

  &:focus {
    outline: none;
  }
`;

const S_TextAreaCounter = styled.p`
  margin-right: 2rem;

  color: ${({ theme }) => theme.FONT.G01};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  text-align: right;
`;
