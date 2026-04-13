import { useState } from 'react';

import styled from '@emotion/styled';
import { useQuery } from '@tanstack/react-query';

import Button from '../../../../common/components/Button/Button';
import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import { authCheckQueryOptions } from '../../../../common/queries/auth';

function CommunityPostCreateForm() {
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [nickname, setNickname] = useState('');
  const [password, setPassword] = useState('');

  const { isPending, isSuccess: isAuthenticatedSuccess } = useQuery(
    authCheckQueryOptions,
  );

  const isFormFilled = isAuthenticatedSuccess
    ? Boolean(title.trim() && content.trim())
    : Boolean(
        title.trim() && content.trim() && nickname.trim() && password.trim(),
      );

  const shouldShowGuestFields = !isAuthenticatedSuccess;

  const handleFormSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
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

        <S_ButtonArea>
          <S_SubmitButton
            type="submit"
            size="full"
            variant={isFormFilled ? 'primary' : 'disabled'}
            disabled={!isFormFilled}
          >
            작성 완료
          </S_SubmitButton>
        </S_ButtonArea>
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
  flex-grow: 1;
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

const S_ButtonArea = styled.div`
  margin-top: auto;
`;

const S_SubmitButton = styled(Button)`
  height: 7.2rem;
  border-radius: 0;

  color: white;

  ${({ theme }) => theme.TYPOGRAPHY.H4_SB};
`;
