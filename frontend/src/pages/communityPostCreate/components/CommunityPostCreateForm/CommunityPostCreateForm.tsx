import { useState } from 'react';

import styled from '@emotion/styled';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import Checkbox from '../../../../common/components/Checkbox/Checkbox';
import CommunityPostForm from '../../../../common/components/CommunityPostForm/CommunityPostForm';
import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import { COMMUNITY_POST } from '../../../../common/constants/communityPost';
import { COMMUNITY_POST_ERROR_MESSAGE } from '../../../../common/constants/communityPost';
import { PAGE_URL } from '../../../../common/constants/url';
import { authCheckQueryOptions } from '../../../../common/queries/auth';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { postCommunityPostDetail } from '../../apis/postCommunityPostDetail';

function CommunityPostCreateForm() {
  const navigate = useNavigate();
  const [nickname, setNickname] = useState('');
  const [guestPassword, setGuestPassword] = useState('');
  const [isAnonymous, setIsAnonymous] = useState(false);

  const { data: authData, isPending } = useQuery(authCheckQueryOptions);
  const isAuthenticated = Boolean(authData?.memberId);

  const { mutate, isPending: isSubmitPending } = useMutation({
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

  if (isPending) {
    return (
      <S_LoadingContainer>
        <LoadingSpinner size="large" />
      </S_LoadingContainer>
    );
  }

  const isGuestPost = !isAuthenticated;
  const isAnonymousPost = isAuthenticated && isAnonymous;
  const shouldRequireIdentity = isGuestPost || isAnonymousPost;
  const shouldRequireNickname = shouldRequireIdentity;
  const shouldRequireGuestPassword = shouldRequireIdentity;
  const shouldShowIdentityFields = shouldRequireIdentity;
  const isNicknameValid =
    nickname.trim().length >= COMMUNITY_POST.NICKNAME.MIN_LENGTH &&
    nickname.trim().length <= COMMUNITY_POST.NICKNAME.MAX_LENGTH;
  const isGuestPasswordValid =
    guestPassword.trim().length === COMMUNITY_POST.GUEST_PASSWORD.LENGTH;
  const isOptionValid =
    !shouldRequireIdentity || (isNicknameValid && isGuestPasswordValid);
  const nicknameErrorMessage =
    shouldRequireNickname && nickname.trim() !== '' && !isNicknameValid
      ? COMMUNITY_POST_ERROR_MESSAGE.NICKNAME_LENGTH
      : '';
  const guestPasswordErrorMessage =
    shouldRequireGuestPassword &&
    guestPassword.trim() !== '' &&
    !isGuestPasswordValid
      ? COMMUNITY_POST_ERROR_MESSAGE.GUEST_PASSWORD_LENGTH
      : '';

  const handleAnonymousChange = (checked: boolean) => {
    setIsAnonymous(checked);

    if (!checked) {
      setNickname('');
      setGuestPassword('');
    }
  };

  const optionSection = (
    <S_Section>
      {shouldShowIdentityFields && <S_Divider />}
      <S_Content>
        {isAuthenticated ? (
          <S_IdentityRow $variant="authenticated">
            <S_CheckboxWrapper>
              <Checkbox
                label="익명"
                checked={isAnonymous}
                onChange={(e) => handleAnonymousChange(e.target.checked)}
              />
            </S_CheckboxWrapper>

            {isAnonymous ? (
              <>
                {shouldRequireNickname ? (
                  <S_IdentityField>
                    <S_FieldInput
                      aria-label="닉네임"
                      value={nickname}
                      maxLength={COMMUNITY_POST.NICKNAME.MAX_LENGTH}
                      placeholder="닉네임을 입력하세요."
                      onChange={(e) => setNickname(e.target.value)}
                    />
                    {nicknameErrorMessage ? (
                      <S_InlineError>{nicknameErrorMessage}</S_InlineError>
                    ) : null}
                  </S_IdentityField>
                ) : null}

                {shouldRequireGuestPassword ? (
                  <S_IdentityField>
                    <S_FieldInput
                      aria-label="비밀번호"
                      type="password"
                      value={guestPassword}
                      maxLength={COMMUNITY_POST.GUEST_PASSWORD.LENGTH}
                      placeholder="비밀번호를 입력하세요."
                      onChange={(e) => setGuestPassword(e.target.value)}
                    />
                    {guestPasswordErrorMessage ? (
                      <S_InlineError>{guestPasswordErrorMessage}</S_InlineError>
                    ) : null}
                  </S_IdentityField>
                ) : null}
              </>
            ) : null}
          </S_IdentityRow>
        ) : (
          <S_IdentityRow $variant="guest">
            {shouldRequireNickname ? (
              <S_IdentityField>
                <S_FieldInput
                  aria-label="닉네임"
                  value={nickname}
                  maxLength={COMMUNITY_POST.NICKNAME.MAX_LENGTH}
                  placeholder="닉네임을 입력하세요."
                  onChange={(e) => setNickname(e.target.value)}
                />
                {nicknameErrorMessage ? (
                  <S_InlineError>{nicknameErrorMessage}</S_InlineError>
                ) : null}
              </S_IdentityField>
            ) : null}

            {shouldRequireGuestPassword ? (
              <S_IdentityField>
                <S_FieldInput
                  aria-label="비밀번호"
                  type="password"
                  value={guestPassword}
                  maxLength={COMMUNITY_POST.GUEST_PASSWORD.LENGTH}
                  placeholder="비밀번호를 입력하세요."
                  onChange={(e) => setGuestPassword(e.target.value)}
                />
                {guestPasswordErrorMessage ? (
                  <S_InlineError>{guestPasswordErrorMessage}</S_InlineError>
                ) : null}
              </S_IdentityField>
            ) : null}
          </S_IdentityRow>
        )}
      </S_Content>
    </S_Section>
  );

  return (
    <CommunityPostForm
      isSubmitPending={isSubmitPending}
      isOptionValid={isOptionValid}
      optionSection={optionSection}
      submitLabel="작성 완료"
      onSavePost={({ title, content }) => {
        mutate({
          isGuestPost,
          postData: {
            title,
            content,
            isAnonymous: isAnonymousPost,
            ...(shouldRequireNickname ? { nickname: nickname.trim() } : {}),
            ...(shouldRequireGuestPassword
              ? { guestPassword: guestPassword.trim() }
              : {}),
          },
        });
      }}
    />
  );
}

export default CommunityPostCreateForm;

const S_LoadingContainer = styled.main`
  display: flex;
  align-items: center;
  justify-content: center;

  min-height: calc(100dvh - 5.7rem);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Section = styled.section`
  display: flex;
  flex-direction: column;
`;

const S_Content = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;

  padding: 1.2rem 1.4rem 1.6rem;
`;

const S_Divider = styled.div`
  width: 100%;
  height: 1px;

  background-color: ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_IdentityRow = styled.div<{ $variant: 'authenticated' | 'guest' }>`
  display: grid;
  align-items: start;
  gap: 0.8rem;
  grid-template-columns: ${({ $variant }) =>
    $variant === 'authenticated'
      ? 'auto minmax(0, 1fr) minmax(0, 1fr)'
      : 'repeat(2, minmax(0, 1fr))'};
`;

const S_CheckboxWrapper = styled.div`
  display: flex;
  align-items: center;

  min-height: 4.4rem;
`;

const S_IdentityField = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 0.4rem;

  min-width: 0;
`;

const S_InlineError = styled.p`
  color: ${({ theme }) => theme.FONT.ERROR};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
`;

const S_FieldInput = styled.input`
  width: 100%;
  height: 4.4rem;
  padding: 0 1.3rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.2rem;

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};

  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  &::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }
`;
