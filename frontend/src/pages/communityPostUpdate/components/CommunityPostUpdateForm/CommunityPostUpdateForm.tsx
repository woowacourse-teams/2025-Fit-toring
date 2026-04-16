import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useLocation, useNavigate, useParams } from 'react-router-dom';

import Checkbox from '../../../../common/components/Checkbox/Checkbox';
import CommunityPostForm from '../../../../common/components/CommunityPostForm/CommunityPostForm';
import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import { PAGE_URL } from '../../../../common/constants/url';
import { authCheckQueryOptions } from '../../../../common/queries/auth';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { getCommunityPostDetail } from '../../../communityPostDetail/apis/getCommunityPostDetail';
import { patchCommunityPost } from '../../apis/patchCommunityPost';

interface CommunityPostUpdateLocationState {
  guestPassword?: string;
}

function CommunityPostUpdateForm() {
  const navigate = useNavigate();
  const { postId } = useParams();
  const location = useLocation();

  const { guestPassword } = (location.state ??
    {}) as CommunityPostUpdateLocationState;

  const [inputGuestPassword, setInputGuestPassword] = useState(
    guestPassword ?? '',
  );

  const {
    data: postData,
    isPending: isPostPending,
    isError: isPostError,
  } = useQuery({
    queryKey: ['communityPostDetailForUpdate', postId],
    queryFn: () => getCommunityPostDetail(postId!),
    enabled: Boolean(postId),
  });

  const shouldRequirePassword = Boolean(
    postData && (postData.isGuestPost || postData.isAnonymous),
  );
  const shouldCheckAuth = Boolean(postData && !shouldRequirePassword);

  const { isPending: isAuthPending, isError: isAuthError } = useQuery({
    ...authCheckQueryOptions,
    enabled: shouldCheckAuth,
  });

  const { mutate: patchCommunityPostMutate, isPending: isSubmitPending } =
    useMutation({
      mutationFn: (values: Parameters<typeof patchCommunityPost>[1]) =>
        patchCommunityPost(postId!, values),
      onSuccess: () => {
        alert('커뮤니티 글이 성공적으로 수정되었습니다.');
        navigate(`${PAGE_URL.COMMUNITY}/${postId}`);
      },
      onError: (error) => {
        alert('커뮤니티 글 수정에 실패했습니다. 다시 시도해주세요.');
        captureSentryError({
          error,
          level: 'warning',
          feature: 'community-post-update',
          step: 'patch-community-post-update',
        });
      },
    });

  useEffect(() => {
    if (isAuthError) {
      navigate(PAGE_URL.LOGIN);
    }
  }, [isAuthError, navigate]);

  useEffect(() => {
    setInputGuestPassword(guestPassword ?? '');
  }, [guestPassword]);

  const handleSavePost = (values: { title: string; content: string }) => {
    if (shouldRequirePassword) {
      patchCommunityPostMutate({
        title: values.title,
        content: values.content,
        guestPassword: inputGuestPassword.trim(),
      });
      return;
    }

    patchCommunityPostMutate({
      title: values.title,
      content: values.content,
    });
  };

  if (isPostPending || (shouldCheckAuth && isAuthPending)) {
    return (
      <S_LoadingContainer>
        <LoadingSpinner size="large" />
      </S_LoadingContainer>
    );
  }

  if (isPostError || !postData) {
    return <div>게시글을 불러오지 못했습니다.</div>;
  }

  const isOptionValid = shouldRequirePassword
    ? Boolean(inputGuestPassword.trim())
    : true;

  const optionSection = shouldRequirePassword ? (
    <S_Section>
      <S_Divider />
      <S_Row>
        <S_RowLabel>닉네임</S_RowLabel>
        <S_Input
          value={postData.nickname}
          placeholder="닉네임을 입력하세요."
          disabled
        />
      </S_Row>
      <S_Row>
        <S_RowLabel>비밀번호</S_RowLabel>
        <S_Input
          type="password"
          value={inputGuestPassword}
          placeholder="비밀번호를 입력하세요."
          onChange={(e) => setInputGuestPassword(e.target.value)}
        />
      </S_Row>

      <S_CheckboxRow>
        <Checkbox
          label="익명"
          checked={postData.isAnonymous}
          disabled
        />
      </S_CheckboxRow>
    </S_Section>
  ) : undefined;

  return (
    <CommunityPostForm
      initialValues={{
        title: postData.title,
        content: postData.content,
      }}
      isSubmitPending={isSubmitPending}
      isOptionValid={isOptionValid}
      optionSection={optionSection}
      submitLabel="수정 완료"
      onSavePost={handleSavePost}
    />
  );
}

export default CommunityPostUpdateForm;

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
  gap: 1.6rem;

  padding: 1.2rem 1.4rem 1.6rem;
`;

const S_Divider = styled.div`
  width: 100%;
  height: 1px;

  background-color: ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_Row = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const S_RowLabel = styled.span`
  flex: 0 0 6.4rem;

  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
`;

const S_CheckboxRow = styled.div`
  display: flex;
  justify-content: flex-end;
`;

const S_Input = styled.input`
  flex: 1;

  height: 4.4rem;
  min-width: 0;
  padding: 0 1.3rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.2rem;

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};

  &:disabled {
    background-color: ${({ theme }) => theme.BG.GRAY};

    color: ${({ theme }) => theme.FONT.G01};
  }

  &::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY300};
  }

  &:focus {
    outline: none;
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }
`;
