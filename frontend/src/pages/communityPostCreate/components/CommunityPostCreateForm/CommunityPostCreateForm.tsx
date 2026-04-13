import styled from '@emotion/styled';
import { useMutation, useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import CommunityPostForm from '../../../../common/components/CommunityPostForm/CommunityPostForm';
import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import { PAGE_URL } from '../../../../common/constants/url';
import { authCheckQueryOptions } from '../../../../common/queries/auth';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { postCommunityPostDetail } from '../../apis/postCommunityPostDetail';

function CommunityPostCreateForm() {
  const navigate = useNavigate();

  const { isPending, isSuccess: isAuthenticated } = useQuery(
    authCheckQueryOptions,
  );

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

  return (
    <CommunityPostForm
      isAuthenticated={isAuthenticated}
      isSubmitPending={isSubmitPending}
      submitLabel="작성 완료"
      onSavePost={(values) => mutate(values)}
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
