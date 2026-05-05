import { useEffect, useState } from 'react';
import type { ChangeEvent, FormEvent } from 'react';

import styled from '@emotion/styled';
import { useMutation, useQueryClient } from '@tanstack/react-query';

import sendIcon from '../../../../common/assets/images/sendIcon.svg';
import Checkbox from '../../../../common/components/Checkbox/Checkbox';
import { COMMUNITY_POST } from '../../../../common/constants/communityPost';
import { BOTTOM_NAV_HEIGHT } from '../../../../common/constants/layout';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { patchCommunityPostComment } from '../../apis/patchCommunityPostComment';
import { postCommunityPostComment } from '../../apis/postCommunityPostComment';

import type { PostComment, PostCommentRequest } from '../../types/postComment';

interface InputSectionProps {
  postId: string;
  authenticated: boolean;
  replyTarget: PostComment | null;
  editingComment: PostComment | null;
  editingCommentGuestPassword?: string;
  onCancelReply: () => void;
  onCancelEdit: () => void;
  onSubmitSuccess: () => void;
  onSubmitEditSuccess: () => void;
}

function InputSection({
  postId,
  authenticated,
  replyTarget,
  editingComment,
  editingCommentGuestPassword,
  onCancelReply,
  onCancelEdit,
  onSubmitSuccess,
  onSubmitEditSuccess,
}: InputSectionProps) {
  const queryClient = useQueryClient();
  const [comment, setComment] = useState('');
  const [nickname, setNickname] = useState('');
  const [guestPassword, setGuestPassword] = useState('');
  const [isAnonymous, setIsAnonymous] = useState(false);
  const [submitAttempted, setSubmitAttempted] = useState(false);
  const isEditMode = editingComment !== null;

  const shouldRequireIdentity = !authenticated || isAnonymous;
  const isAnonymousComment = !authenticated || isAnonymous;

  const isNicknameValid =
    nickname.trim().length >= COMMUNITY_POST.NICKNAME.MIN_LENGTH &&
    nickname.trim().length <= COMMUNITY_POST.NICKNAME.MAX_LENGTH;
  const isPasswordValid =
    guestPassword.trim().length === COMMUNITY_POST.GUEST_PASSWORD.LENGTH;

  const isFormValid = isEditMode
    ? comment.trim().length > 0
    : comment.trim().length > 0 &&
      (!shouldRequireIdentity || (isNicknameValid && isPasswordValid));

  const { mutate: postCommentMutate, isPending: isSubmitPending } = useMutation(
    {
      mutationFn: (commentData: PostCommentRequest) =>
        postCommunityPostComment(postId, commentData),
      onSuccess: async () => {
        await queryClient.invalidateQueries({
          queryKey: ['postComments', postId],
        });
        await queryClient.invalidateQueries({
          queryKey: ['communityPostDetail', postId],
        });

        setComment('');
        setNickname('');
        setGuestPassword('');
        setSubmitAttempted(false);
        onCancelReply();
        onSubmitSuccess();
      },
      onError: (error) => {
        captureSentryError({
          error,
          level: 'warning',
          feature: 'community-post-detail',
          step: 'community-post-comment-create',
        });
        alert('댓글 등록에 실패했습니다. 다시 시도해주세요.');
      },
    },
  );

  const { mutate: patchCommentMutate, isPending: isEditSubmitPending } =
    useMutation({
      mutationFn: (commentData: { content: string; guestPassword?: string }) =>
        patchCommunityPostComment(editingComment!.id, commentData),
      onSuccess: async () => {
        await queryClient.invalidateQueries({
          queryKey: ['postComments', postId],
        });
        await queryClient.invalidateQueries({
          queryKey: ['communityPostDetail', postId],
        });

        setComment('');
        setSubmitAttempted(false);
        onCancelEdit();
        onSubmitEditSuccess();
      },
      onError: (error) => {
        captureSentryError({
          error,
          level: 'warning',
          feature: 'community-post-detail',
          step: 'community-post-comment-update',
        });
        alert('댓글 수정에 실패했습니다. 다시 시도해주세요.');
      },
    });

  const replyLabel = replyTarget
    ? `@${replyTarget.nickname}에게 답글 작성 중`
    : '';
  const editLabel = editingComment ? '댓글 수정 중' : '';
  let commentPlaceholder = '댓글을 입력해주세요.';

  if (replyTarget) {
    commentPlaceholder = '답글을 입력해주세요.';
  }

  if (isEditMode) {
    commentPlaceholder = '수정할 내용을 입력해주세요.';
  }

  const nicknameErrorMessage = (() => {
    if (!shouldRequireIdentity) {
      return '';
    }

    if (submitAttempted && nickname.trim() === '') {
      return '닉네임을 입력해주세요.';
    }

    if (nickname.trim() !== '' && !isNicknameValid) {
      return `닉네임은 ${COMMUNITY_POST.NICKNAME.MIN_LENGTH}자 이상 ${COMMUNITY_POST.NICKNAME.MAX_LENGTH}자 이하로 입력해주세요.`;
    }

    return '';
  })();

  const passwordErrorMessage = (() => {
    if (!shouldRequireIdentity) {
      return '';
    }

    if (submitAttempted && guestPassword.trim() === '') {
      return '비밀번호를 입력해주세요.';
    }

    if (guestPassword.trim() !== '' && !isPasswordValid) {
      return `비밀번호는 ${COMMUNITY_POST.GUEST_PASSWORD.LENGTH}자로 입력해주세요.`;
    }

    return '';
  })();

  const handleCommentChange = (e: ChangeEvent<HTMLInputElement>) => {
    setComment(e.target.value);
  };

  const handleAnonymousChange = (e: ChangeEvent<HTMLInputElement>) => {
    const checked = e.target.checked;

    setIsAnonymous(checked);

    if (!checked) {
      setNickname('');
      setGuestPassword('');
      setSubmitAttempted(false);
      return;
    }
  };

  const handleCommentSubmit = (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setSubmitAttempted(true);

    if (isEditMode) {
      if (!isFormValid || isEditSubmitPending) {
        return;
      }

      patchCommentMutate({
        content: comment.trim(),
        ...(editingComment?.isGuestComment || editingComment?.isAnonymous
          ? { guestPassword: editingCommentGuestPassword }
          : {}),
      });
      return;
    }

    if (!isFormValid || isSubmitPending) {
      return;
    }

    postCommentMutate({
      content: comment.trim(),
      isAnonymous: isAnonymousComment,
      ...(shouldRequireIdentity
        ? {
            nickname: nickname.trim(),
            guestPassword: guestPassword.trim(),
          }
        : {}),
      parentId: replyTarget?.id ?? null,
      rootId: replyTarget ? (replyTarget.rootId ?? replyTarget.id) : null,
    });
  };

  useEffect(() => {
    if (editingComment) {
      setComment(editingComment.content);
      setSubmitAttempted(false);
      setIsAnonymous(false);
      return;
    }

    setComment('');
    setGuestPassword('');
    setNickname('');
    setSubmitAttempted(false);
    setIsAnonymous(false);
  }, [editingComment]);

  return (
    <S_Container>
      <S_Form onSubmit={handleCommentSubmit}>
        {isEditMode ? (
          <S_ReplyBanner>
            <S_ReplyText>{editLabel}</S_ReplyText>
            <S_ReplyCancelButton type="button" onClick={onCancelEdit}>
              취소
            </S_ReplyCancelButton>
          </S_ReplyBanner>
        ) : null}

        {!isEditMode && replyTarget ? (
          <S_ReplyBanner>
            <S_ReplyText>{replyLabel}</S_ReplyText>
            <S_ReplyCancelButton type="button" onClick={onCancelReply}>
              취소
            </S_ReplyCancelButton>
          </S_ReplyBanner>
        ) : null}

        {!isEditMode && authenticated ? (
          <S_IdentitySection>
            <S_IdentityRow $variant="authenticated">
              <S_CheckboxWrapper>
                <Checkbox
                  label="익명"
                  checked={isAnonymous}
                  onChange={handleAnonymousChange}
                />
              </S_CheckboxWrapper>

              {isAnonymous ? (
                <>
                  <S_IdentityField>
                    <S_FieldInput
                      id="comment-nickname"
                      value={nickname}
                      maxLength={COMMUNITY_POST.NICKNAME.MAX_LENGTH}
                      placeholder="닉네임을 입력하세요."
                      onChange={(e) => setNickname(e.target.value)}
                    />
                    {nicknameErrorMessage ? (
                      <S_InlineError>{nicknameErrorMessage}</S_InlineError>
                    ) : null}
                  </S_IdentityField>

                  <S_IdentityField>
                    <S_FieldInput
                      id="comment-password"
                      type="password"
                      value={guestPassword}
                      maxLength={COMMUNITY_POST.GUEST_PASSWORD.LENGTH}
                      placeholder="비밀번호를 입력하세요."
                      onChange={(e) => setGuestPassword(e.target.value)}
                    />
                    {passwordErrorMessage ? (
                      <S_InlineError>{passwordErrorMessage}</S_InlineError>
                    ) : null}
                  </S_IdentityField>
                </>
              ) : null}
            </S_IdentityRow>
          </S_IdentitySection>
        ) : null}

        {!isEditMode && !authenticated ? (
          <S_IdentitySection>
            <S_IdentityRow $variant="guest">
              <S_IdentityField>
                <S_FieldInput
                  id="comment-nickname"
                  value={nickname}
                  maxLength={COMMUNITY_POST.NICKNAME.MAX_LENGTH}
                  placeholder="닉네임을 입력하세요."
                  onChange={(e) => setNickname(e.target.value)}
                />
                {nicknameErrorMessage ? (
                  <S_InlineError>{nicknameErrorMessage}</S_InlineError>
                ) : null}
              </S_IdentityField>

              <S_IdentityField>
                <S_FieldInput
                  id="comment-password"
                  type="password"
                  value={guestPassword}
                  maxLength={COMMUNITY_POST.GUEST_PASSWORD.LENGTH}
                  placeholder="비밀번호를 입력하세요."
                  onChange={(e) => setGuestPassword(e.target.value)}
                />
                {passwordErrorMessage ? (
                  <S_InlineError>{passwordErrorMessage}</S_InlineError>
                ) : null}
              </S_IdentityField>
            </S_IdentityRow>
          </S_IdentitySection>
        ) : null}

        <S_CommentRow>
          <S_CommentInput
            value={comment}
            placeholder={commentPlaceholder}
            onChange={handleCommentChange}
          />
          <S_SubmitButton
            type="submit"
            disabled={
              !isFormValid ||
              isSubmitPending ||
              (isEditMode && isEditSubmitPending)
            }
            aria-label={isEditMode ? '댓글 수정' : '댓글 등록'}
          >
            <S_SendIcon src={sendIcon} alt="" />
          </S_SubmitButton>
        </S_CommentRow>
      </S_Form>
    </S_Container>
  );
}

export default InputSection;

const S_Container = styled.div`
  position: fixed;
  bottom: ${BOTTOM_NAV_HEIGHT}rem;
  left: 50%;
  z-index: 1;

  width: 48rem;
  border-right: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-left: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  box-shadow: 0 -0.4rem 1.6rem rgb(0 0 0 / 5%);

  background-color: ${({ theme }) => theme.BG.WHITE};
  transform: translateX(-50%);

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;

const S_Form = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  padding: 1rem 1.6rem 1.2rem;
`;

const S_ReplyBanner = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
`;

const S_ReplyText = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const S_ReplyCancelButton = styled.button`
  padding: 0;
  border: none;

  background: transparent;

  color: ${({ theme }) => theme.FONT.B04};
  cursor: pointer;
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const S_IdentitySection = styled.section`
  display: flex;
  flex-direction: column;
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

const S_CommentRow = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;

  padding: 0.8rem 1rem;
  border-radius: 999px;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_CommentInput = styled.input`
  flex: 1;

  min-width: 0;
  padding: 0;
  border: none;

  background-color: transparent;

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};

  &:focus {
    outline: none;
  }

  &::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }
`;

const S_SubmitButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 3.2rem;
  height: 3.2rem;
  padding: 0;
  border: none;
  border-radius: 50%;

  cursor: pointer;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY400};

  &:disabled {
    cursor: not-allowed;
    opacity: 0.35;
  }
`;

const S_SendIcon = styled.img`
  width: 1.4rem;
  height: 1.4rem;
`;
