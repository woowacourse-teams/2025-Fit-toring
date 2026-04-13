import { useEffect, useState } from 'react';
import type { FormEvent } from 'react';

import styled from '@emotion/styled';

import Button from '../Button/Button';

import type { CommunityPostFormValues } from '../../types/communityPostForm';

interface CommunityPostFormProps {
  initialValues?: CommunityPostFormValues;
  onSavePost: (values: CommunityPostFormValues) => void;
  isSubmitPending?: boolean;
  isAuthenticated: boolean;
  submitLabel?: string;
}

function CommunityPostForm({
  initialValues,
  onSavePost,
  isSubmitPending = false,
  isAuthenticated,
  submitLabel = '작성 완료',
}: CommunityPostFormProps) {
  const [title, setTitle] = useState(initialValues?.title ?? '');
  const [content, setContent] = useState(initialValues?.content ?? '');
  const [nickname, setNickname] = useState(initialValues?.nickname ?? '');
  const [guestPassword, setGuestPassword] = useState(
    initialValues?.guestPassword ?? '',
  );
  const [isAnonymous, setIsAnonymous] = useState(
    initialValues?.isAnonymous ?? false,
  );

  useEffect(() => {
    setTitle(initialValues?.title ?? '');
    setContent(initialValues?.content ?? '');
    setNickname(initialValues?.nickname ?? '');
    setGuestPassword(initialValues?.guestPassword ?? '');
    setIsAnonymous(initialValues?.isAnonymous ?? false);
  }, [initialValues]);

  const shouldShowGuestFields = !isAuthenticated;
  const isFormFilled = shouldShowGuestFields
    ? Boolean(
        title.trim() &&
        content.trim() &&
        nickname.trim() &&
        guestPassword.trim(),
      )
    : Boolean(title.trim() && content.trim());

  const handleFormSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!isFormFilled || isSubmitPending) {
      return;
    }

    const values: CommunityPostFormValues = {
      title: title.trim(),
      content: content.trim(),
      isAnonymous,
      ...(shouldShowGuestFields
        ? {
            nickname: nickname.trim(),
            guestPassword: guestPassword.trim(),
          }
        : {}),
    };

    onSavePost(values);
  };

  return (
    <S_Container>
      <S_Form onSubmit={handleFormSubmit}>
        <S_EditorSection>
          <S_TitleInput
            value={title}
            placeholder="제목을 입력하세요."
            onChange={(e) => setTitle(e.target.value)}
          />
          <S_Divider />
          <S_ContentInput
            value={content}
            placeholder="내용을 입력해주세요."
            onChange={(e) => setContent(e.target.value)}
          />
        </S_EditorSection>

        {shouldShowGuestFields && (
          <S_BottomArea>
            <S_ExtraInput
              value={nickname}
              placeholder="닉네임을 입력하세요."
              onChange={(e) => setNickname(e.target.value)}
            />
            <S_ExtraInput
              type="password"
              value={guestPassword}
              placeholder="비밀번호를 입력하세요."
              onChange={(e) => setGuestPassword(e.target.value)}
            />
          </S_BottomArea>
        )}

        <S_OptionRow>
          <S_CheckboxLabel>
            <S_CheckboxInput
              type="checkbox"
              checked={isAnonymous}
              onChange={(e) => setIsAnonymous(e.target.checked)}
            />
            <S_CheckboxIndicator checked={isAnonymous} />
            <S_CheckboxText>익명</S_CheckboxText>
          </S_CheckboxLabel>
        </S_OptionRow>
        <S_SubmitButton
          type="submit"
          size="full"
          variant={isFormFilled && !isSubmitPending ? 'primary' : 'disabled'}
          disabled={!isFormFilled || isSubmitPending}
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
  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  line-height: 1.65;
  resize: none;

  &::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  &:focus {
    outline: none;
  }
`;

const S_BottomArea = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  gap: 0.8rem;

  padding: 1.2rem 1.4rem;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_ExtraInput = styled.input`
  width: 100%;
  height: 4.4rem;
  padding: 0 1.3rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.2rem;

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};

  &::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY300};
  }

  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }
`;

const S_OptionRow = styled.div`
  display: flex;
  justify-content: flex-end;

  padding: 0 1.4rem 1.2rem;
`;

const S_CheckboxLabel = styled.label`
  display: inline-flex;
  align-items: center;
  gap: 0.8rem;

  cursor: pointer;
`;

const S_CheckboxInput = styled.input`
  display: none;
`;

const S_CheckboxIndicator = styled.span<{ checked: boolean }>`
  display: inline-flex;
  align-items: center;
  justify-content: center;

  width: 1.8rem;
  height: 1.8rem;
  border: 1px solid
    ${({ theme, checked }) =>
      checked ? theme.SYSTEM.MAIN500 : theme.OUTLINE.DARK};
  border-radius: 0.4rem;

  background-color: ${({ theme, checked }) =>
    checked ? theme.SYSTEM.MAIN500 : theme.BG.WHITE};

  &::after {
    content: '';

    width: 0.5rem;
    height: 0.9rem;
    border-right: 2px solid ${({ theme }) => theme.BG.WHITE};
    border-bottom: 2px solid ${({ theme }) => theme.BG.WHITE};
    opacity: ${({ checked }) => (checked ? 1 : 0)};
    transform: rotate(45deg) translate(-1px, -1px);
  }
`;

const S_CheckboxText = styled.span`
  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
`;

const S_SubmitButton = styled(Button)`
  height: 7.2rem;
  border-radius: 0;

  color: white;

  ${({ theme }) => theme.TYPOGRAPHY.H4_SB};
`;
