import { useState } from 'react';

import styled from '@emotion/styled';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';
import { PAGE_URL } from '../../common/constants/url';
import { captureSentryError } from '../../common/utils/captureSentryError';
import CommunityPostDeleteModal from '../community/components/CommunityPostDeleteModal/CommunityPostDeleteModal';
import CommunityPostPasswordModal from '../community/components/CommunityPostPasswordModal/CommunityPostPasswordModal';

import { deleteCommunityPost } from './apis/deleteCommunityPost';
import { getCommunityPostDetail } from './apis/getCommunityPostDetail';
import { postGuestPostPasswordCheck } from './apis/postGuestPostPasswordCheck';
import CommunityPostDetailHeader from './components/CommunityPostDetailHeader/CommunityPostDetailHeader';
import InputSection from './components/InputSection/InputSection';
import PostCommentSection from './components/PostCommentSection/PostCommentSection';
import PostContent from './components/PostContent/PostContent';
import PostHeader from './components/PostHeader/PostHeader';

type PendingAction = 'edit' | 'delete' | null;

function CommunityPostDetail() {
  const { postId } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { authenticated } = useAuth();

  const [passwordModalOpened, setPasswordModalOpened] = useState(false);
  const [deleteModalOpened, setDeleteModalOpened] = useState(false);
  const [pendingAction, setPendingAction] = useState<PendingAction>(null);

  const {
    data: postData,
    isPending,
    isError,
  } = useQuery({
    queryKey: ['communityPostDetail', postId],
    queryFn: () => getCommunityPostDetail(postId!),
    enabled: Boolean(postId),
  });

  const { mutate: deletePostMutate } = useMutation({
    mutationFn: deleteCommunityPost,
    onSuccess: () => {
      setDeleteModalOpened(false);
      alert('게시글 삭제에 성공했습니다.');
      navigate(PAGE_URL.COMMUNITY);
    },
    onError: (error) => {
      alert('게시글 삭제에 실패했습니다.');
      captureSentryError({
        error,
        level: 'warning',
        feature: 'community-post-detail',
        step: 'community-post-delete',
      });
    },
  });

  if (isPending) {
    return <LoadingSpinner />;
  }

  if (isError || !postData) {
    return <div>게시글을 불러오지 못했습니다.</div>;
  }

  const canManagePost =
    postData.isGuestPost || (authenticated && postData.isMine);

  const openPasswordModal = () => {
    setPasswordModalOpened(true);
  };

  const openDeleteModal = () => {
    setDeleteModalOpened(true);
  };

  const handleCloseClickPasswordModal = () => {
    setPasswordModalOpened(false);
    setPendingAction(null);
  };

  const handleCloseClickDeleteModal = () => {
    setDeleteModalOpened(false);
  };

  const handleConfirmClickPasswordModal = async (password: string) => {
    try {
      await queryClient.fetchQuery({
        queryKey: ['guestPostPasswordCheck', postId, password],
        queryFn: () =>
          postGuestPostPasswordCheck({
            postId: postId!,
            guestPassword: password,
          }),
        retry: false,
      });
      setPasswordModalOpened(false);

      if (pendingAction === 'delete') {
        openDeleteModal();
        setPendingAction(null);
        return;
      }

      setPendingAction(null);
      navigate(`${PAGE_URL.COMMUNITY}/${postId}${PAGE_URL.EDIT}`);
    } catch {
      alert('비밀번호가 일치하지 않습니다.');
    }
  };

  const handleEditClick = () => {
    if (postData.isGuestPost) {
      setPendingAction('edit');
      openPasswordModal();
      return;
    }

    navigate(`${PAGE_URL.COMMUNITY}/${postId}${PAGE_URL.EDIT}`);
  };

  const handleDeleteClick = () => {
    if (postData.isGuestPost) {
      setPendingAction('delete');
      openPasswordModal();
      return;
    }

    openDeleteModal();
  };

  const handleConfirmClickDeleteModal = () => {
    if (postData.isGuestPost) {
      return;
    }

    deletePostMutate({ postId: postId! });
  };

  return (
    <S_Container>
      <CommunityPostDetailHeader
        showActionButton={canManagePost}
        onEditClick={handleEditClick}
        onDeleteClick={handleDeleteClick}
      />
      <S_Content>
        <PostHeader
          createdAt={postData.createdAt}
          nickname={postData.isAnonymous ? '익명' : postData.nickname}
          viewCount={postData.viewCount}
        />
        <PostContent
          title={postData.title}
          content={postData.content}
          likeCount={postData.likeCount}
        />
        <PostCommentSection postId={postId ?? ''} />
      </S_Content>
      <InputSection />
      <CommunityPostPasswordModal
        opened={passwordModalOpened}
        onCloseClick={handleCloseClickPasswordModal}
        onConfirmClick={handleConfirmClickPasswordModal}
        title="비밀번호 확인"
        description={`비회원 게시글을 ${pendingAction === 'delete' ? '삭제' : '수정'}하려면 비밀번호를 입력해주세요.`}
        confirmLabel={pendingAction === 'delete' ? '삭제하기' : '수정하기'}
      />
      <CommunityPostDeleteModal
        opened={deleteModalOpened}
        onCloseClick={handleCloseClickDeleteModal}
        onConfirmClick={handleConfirmClickDeleteModal}
      />
    </S_Container>
  );
}

export default CommunityPostDetail;

const S_Container = styled.main`
  display: flex;
  flex-direction: column;

  min-height: 100dvh;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Content = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;

  padding-bottom: 7rem;
`;
