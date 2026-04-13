import { useState } from 'react';

import styled from '@emotion/styled';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import Button from '../../../../common/components/Button/Button';
import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import { PAGE_URL } from '../../../../common/constants/url';
import { authCheckQueryOptions } from '../../../../common/queries/auth';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { postCommunityPostDetail } from '../../apis/postCommunityPostDetail';

function CommunityPostCreateForm() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [nickname, setNickname] = useState('');
  const [password, setPassword] = useState('');
  const [anonymousChecked, setAnonymousChecked] = useState(false);

  const navigate = useNavigate();

  const { isPending, isSuccess: isAuthenticatedSuccess } = useQuery(
    authCheckQueryOptions,
  );
  const { mutate: postCommunityPostDetailMutate, isPending: isSubmitPending } =
    useMutation({
      mutationFn: postCommunityPostDetail,
      onSuccess: () => {
        alert('커뮤니티 글이 성공적으로 등록되었습니다.');
        navigate(PAGE_URL.COMMUNITY);
      },
      onError: (error) => {
        alert('커뮤니티 글 등록에 실패했습니다. 다시 시도해주세요.');
        captureSentryError({
          error,
          level: 'warning',
          feature: 'community-post-create',
          step: 'post-community-post-create',
        });
      },
    });

  const isFormFilled = isAuthenticatedSuccess
    ? Boolean(title.trim() && content.trim())
    : Boolean(
        title.trim() && content.trim() && nickname.trim() && password.trim(),
      );

  const shouldShowGuestFields = !isAuthenticatedSuccess;

  const handleFormSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!isFormFilled) {
      return;
    }

    const postData = {
      title: title.trim(),
      content: content.trim(),
      isAnonymous: anonymousChecked,
      ...(shouldShowGuestFields
        ? {
            nickname: nickname.trim(),
            guestPassword: password.trim(),
          }
        : {}),
    };

    postCommunityPostDetailMutate(postData);
  };

  const handleTitleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setTitle(e.target.value);
  };

  const handleContentChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setContent(e.target.value);
  };

  const handleNicknameChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNickname(e.target.value);
  };

  const handlePasswordChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setPassword(e.target.value);
  };

  const handleAnonymousCheckChange = (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    setAnonymousChecked(e.target.checked);
  };

  if (isPending) {
    return (
      <S_LoadingContainer>
        <LoadingSpinner size="large" />
      </S_LoadingContainer>
    );
  }

  return (
    <S_Container>
      <S_Form onSubmit={handleFormSubmit}>
        <S_EditorSection>
          <S_TitleInput
            value={title}
            placeholder="제목을 입력하세요."
            onChange={handleTitleChange}
          />
          <S_Divider />
          <S_ContentInput
            value={content}
            placeholder={`내용을 입력해주세요.`}
            onChange={handleContentChange}
          />
        </S_EditorSection>

        {shouldShowGuestFields && (
          <S_BottomArea>
            <S_ExtraInput
              value={nickname}
              placeholder="닉네임을 입력하세요."
              onChange={handleNicknameChange}
            />
            <S_ExtraInput
              type="password"
              value={password}
              placeholder="비밀번호를 입력하세요."
              onChange={handlePasswordChange}
            />
          </S_BottomArea>
        )}

        <S_OptionRow>
          <S_CheckboxLabel>
            <S_CheckboxInput
              type="checkbox"
              checked={anonymousChecked}
              onChange={handleAnonymousCheckChange}
            />
            <S_CheckboxIndicator checked={anonymousChecked} />
            <S_CheckboxText>익명</S_CheckboxText>
          </S_CheckboxLabel>
        </S_OptionRow>
        <S_SubmitButton
          type="submit"
          size="full"
          variant={isFormFilled && !isSubmitPending ? 'primary' : 'disabled'}
          disabled={!isFormFilled || isSubmitPending}
        >
          작성 완료
        </S_SubmitButton>
      </S_Form>
    </S_Container>
  );
}

export default CommunityPostCreateForm;

const S_Container = styled.main`
  min-height: calc(100dvh - 5.7rem);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_LoadingContainer = styled.main`
  display: flex;
  align-items: center;
  justify-content: center;

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
