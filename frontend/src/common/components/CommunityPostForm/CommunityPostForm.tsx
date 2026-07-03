import { useState } from 'react';
import type { FormEvent, ReactNode } from 'react';

import styled from '@emotion/styled';

import { COMMUNITY_POST } from '../../constants/communityPost';
import Button from '../Button/Button';

type CommunityPostFormInitialValues = {
  title?: string;
  content?: string;
};

interface CommunityPostFormProps {
  initialValues?: CommunityPostFormInitialValues;
  onSavePost: (values: {
    title: string;
    content: string;
  }) => void;
  isOptionValid: boolean;
  isSubmitPending?: boolean;
  optionSection?: ReactNode;
  submitLabel?: string;
}

function CommunityPostForm({
  initialValues,
  onSavePost,
  isOptionValid,
  isSubmitPending = false,
  optionSection,
  submitLabel = '작성 완료',
}: CommunityPostFormProps) {
  const [title, setTitle] = useState(initialValues?.title ?? '');
  const [content, setContent] = useState(initialValues?.content ?? '');

  const isEditorFilled = Boolean(title.trim() && content.trim());
  const isSubmitEnabled = isEditorFilled && isOptionValid;

  const handleFormSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!isSubmitEnabled || isSubmitPending) {
      return;
    }

    onSavePost({
      title: title.trim(),
      content: content.trim(),
    });
  };

  return (
    <S_Container>
      <S_Form onSubmit={handleFormSubmit}>
        <S_EditorSection>
          <S_TitleInput
            value={title}
            placeholder="제목을 입력하세요."
            maxLength={COMMUNITY_POST.TITLE.MAX_LENGTH}
            onChange={(e) => setTitle(e.target.value)}
          />
          <S_Divider />
          <S_ContentInput
            value={content}
            placeholder="내용을 입력해주세요."
            maxLength={COMMUNITY_POST.CONTENT.MAX_LENGTH}
            onChange={(e) => setContent(e.target.value)}
          />
        </S_EditorSection>

        {optionSection}
        <S_SubmitButton
          type="submit"
          size="full"
          variant={isSubmitEnabled && !isSubmitPending ? 'primary' : 'disabled'}
          disabled={!isSubmitEnabled || isSubmitPending}
        >
          {submitLabel}
        </S_SubmitButton>
      </S_Form>
    </S_Container>
  );
}

export default CommunityPostForm;

const S_Container = styled.main`
  min-height: calc(100dvh - 5.7rem);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Form = styled.form`
  display: flex;
  flex-direction: column;

  min-height: calc(100dvh - 5.7rem);
`;

const S_EditorSection = styled.section`
  display: flex;
  flex: 1;
  flex-direction: column;

  min-height: 0;
`;

const S_TitleInput = styled.input`
  width: 100%;
  padding: 2rem 1.4rem 1.5rem;
  border: none;

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_SB};

  &::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  &:focus {
    outline: none;
  }
`;

const S_Divider = styled.div`
  width: 100%;
  height: 1px;

  background-color: ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_ContentInput = styled.textarea`
  flex-grow: 1;

  width: 100%;
  min-height: 24rem;
  padding: 1.8rem 1.4rem;
  border: none;
  overflow-y: auto;

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
  line-height: 1.65;
  resize: none;

  &::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  &:focus {
    outline: none;
  }
`;

const S_SubmitButton = styled(Button)`
  height: 7.2rem;
  border-radius: 0;

  color: white;

  ${({ theme }) => theme.TYPOGRAPHY.H4_SB};
`;
