import { useState } from 'react';

import styled from '@emotion/styled';
import {
  type InfiniteData,
  useMutation,
  useQuery,
  useQueryClient,
} from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import DeleteConfirmModal from '../../common/components/DeleteConfirmModal/DeleteConfirmModal';
import LoadingSpinner from '../../common/components/LoadingSpinner/LoadingSpinner';
import { PAGE_URL } from '../../common/constants/url';
import { captureSentryError } from '../../common/utils/captureSentryError';
import CommunityPostPasswordModal from '../community/components/CommunityPostPasswordModal/CommunityPostPasswordModal';

import { deleteCommunityPost } from './apis/deleteCommunityPost';
import { deleteCommunityPostComment } from './apis/deleteCommunityPostComment';
import { getCommunityPostDetail } from './apis/getCommunityPostDetail';
import { postCommunityPostCommentGuestCheck } from './apis/postCommunityPostCommentGuestCheck';
import {
  deleteCommunityPostLike,
  postCommunityPostLike,
} from './apis/postCommunityPostLike';
import { postGuestPostPasswordCheck } from './apis/postGuestPostPasswordCheck';
import CommunityPostDetailHeader from './components/CommunityPostDetailHeader/CommunityPostDetailHeader';
import InputSection from './components/InputSection/InputSection';
import PostCommentSection from './components/PostCommentSection/PostCommentSection';
import PostContent from './components/PostContent/PostContent';
import PostHeader from './components/PostHeader/PostHeader';

import type { PostComment } from './types/postComment';
import type { CommunityPostDetail } from '../../common/types/communityPost';
import type { CommunityPostResponse } from '../community/types/posts';

type PendingAction = 'edit' | 'delete' | null;

const updateCommunityPostsLikeCache = (
  currentData: InfiniteData<CommunityPostResponse> | undefined,
  postId: number,
  liked: boolean,
  likeCount: number,
) => {
  if (!currentData) {
    return currentData;
  }

  return {
    ...currentData,
    pages: currentData.pages.map((page) => ({
      ...page,
      posts: page.posts.map((post) =>
        post.id === postId ? { ...post, liked, likeCount } : post,
      ),
    })),
  };
};

function CommunityPostDetail() {
  const { postId } = useParams();
  const navigate = useNavigate();
  const queryClient = useQueryClient();
  const { authenticated } = useAuth();

  const [passwordModalOpened, setPasswordModalOpened] = useState(false);
  const [deleteModalOpened, setDeleteModalOpened] = useState(false);
  const [pendingAction, setPendingAction] = useState<PendingAction>(null);
  const [pendingDeletePassword, setPendingDeletePassword] = useState('');
  const [replyTarget, setReplyTarget] = useState<PostComment | null>(null);
  const [editingComment, setEditingComment] = useState<PostComment | null>(null);
  const [editingCommentGuestPassword, setEditingCommentGuestPassword] =
    useState('');
  const [commentPasswordModalOpened, setCommentPasswordModalOpened] =
    useState(false);
  const [pendingCommentAction, setPendingCommentAction] =
    useState<PendingAction>(null);
  const [pendingCommentTarget, setPendingCommentTarget] =
    useState<PostComment | null>(null);
  const [commentDeleteModalOpened, setCommentDeleteModalOpened] =
    useState(false);
  const [commentDeleteTarget, setCommentDeleteTarget] =
    useState<PostComment | null>(null);
  const [commentDeletePassword, setCommentDeletePassword] = useState('');

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
      setPendingDeletePassword('');
      setPendingAction(null);
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

  const { mutate: deleteCommentMutate } = useMutation({
    mutationFn: ({
      commentId,
      guestPassword,
    }: {
      commentId: number;
      guestPassword?: string;
    }) => deleteCommunityPostComment(commentId, guestPassword),
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ['postComments', postId],
      });
      await queryClient.invalidateQueries({
        queryKey: ['communityPostDetail', postId],
      });

      setCommentDeleteModalOpened(false);
      setCommentDeleteTarget(null);
      setCommentDeletePassword('');
      setEditingComment(null);
      setEditingCommentGuestPassword('');
      alert('댓글 삭제에 성공했습니다.');
    },
    onError: (error) => {
      alert('댓글 삭제에 실패했습니다.');
      captureSentryError({
        error,
        level: 'warning',
        feature: 'community-post-detail',
        step: 'community-post-comment-delete',
      });
    },
  });

  const { mutate: togglePostLikeMutate, isPending: isPostLikePending } =
    useMutation({
      mutationFn: async () => {
        if (postData.liked) {
          return await deleteCommunityPostLike(postId!);
        }

        return await postCommunityPostLike(postId!);
      },
      onSuccess: (updatedPost) => {
        queryClient.setQueryData<CommunityPostDetail>(
          ['communityPostDetail', postId],
          (currentPost) =>
            currentPost
              ? {
                  ...currentPost,
                  liked: updatedPost.liked,
                  likeCount: updatedPost.likeCount,
                }
              : currentPost,
        );

        queryClient.setQueryData<InfiniteData<CommunityPostResponse>>(
          ['communityPosts'],
          (currentData) =>
            updateCommunityPostsLikeCache(
              currentData,
              updatedPost.postId,
              updatedPost.liked,
              updatedPost.likeCount,
            ),
        );
      },
      onError: (error) => {
        alert('게시글 좋아요에 실패했습니다.');
        captureSentryError({
          error,
          level: 'warning',
          feature: 'community-post-detail',
          step: 'community-post-like',
        });
      },
    });

  if (isPending) {
    return <LoadingSpinner />;
  }

  if (isError || !postData) {
    return <div>게시글을 불러오지 못했습니다.</div>;
  }

  const shouldRequirePassword =
    postData.isGuestPost || postData.isAnonymous;
  const canManagePost =
    shouldRequirePassword || (authenticated && postData.isMine);
  const isGuestLikeComment = (comment: PostComment) =>
    comment.isGuestComment || comment.isAnonymous;

  const openPasswordModal = () => {
    setPasswordModalOpened(true);
  };

  const openDeleteModal = () => {
    setDeleteModalOpened(true);
  };

  const handleCloseClickPasswordModal = () => {
    setPasswordModalOpened(false);
    setPendingAction(null);
    setPendingDeletePassword('');
  };

  const handleCloseClickDeleteModal = () => {
    setDeleteModalOpened(false);
    setPendingDeletePassword('');
  };

  const handleCloseClickCommentPasswordModal = () => {
    setCommentPasswordModalOpened(false);
    setPendingCommentAction(null);
    setPendingCommentTarget(null);
  };

  const handleCloseClickCommentDeleteModal = () => {
    setCommentDeleteModalOpened(false);
    setCommentDeleteTarget(null);
    setCommentDeletePassword('');
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
        setPendingDeletePassword(password);
        openDeleteModal();
        setPendingAction(null);
        return;
      }

      setPendingAction(null);
      navigate(`${PAGE_URL.COMMUNITY}/${postId}${PAGE_URL.EDIT}`, {
        state: { guestPassword: password },
      });
    } catch {
      alert('비밀번호가 일치하지 않습니다.');
    }
  };

  const handleEditClick = () => {
    if (shouldRequirePassword) {
      setPendingAction('edit');
      openPasswordModal();
      return;
    }

    navigate(`${PAGE_URL.COMMUNITY}/${postId}${PAGE_URL.EDIT}`);
  };

  const handleDeleteClick = () => {
    if (shouldRequirePassword) {
      setPendingAction('delete');
      openPasswordModal();
      return;
    }

    openDeleteModal();
  };

  const handleReplyClick = (comment: PostComment) => {
    setReplyTarget(comment);
    setEditingComment(null);
    setEditingCommentGuestPassword('');
  };

  const handleEditCommentClick = (comment: PostComment) => {
    setReplyTarget(null);
    setEditingComment(null);
    setEditingCommentGuestPassword('');
    setCommentDeleteModalOpened(false);
    setCommentDeleteTarget(null);
    setCommentDeletePassword('');

    if (isGuestLikeComment(comment)) {
      setPendingCommentAction('edit');
      setPendingCommentTarget(comment);
      setCommentPasswordModalOpened(true);
      return;
    }

    if (comment.isMine) {
      setEditingComment(comment);
      setEditingCommentGuestPassword('');
    }
  };

  const handleDeleteCommentClick = (comment: PostComment) => {
    setReplyTarget(null);
    setEditingComment(null);
    setEditingCommentGuestPassword('');

    if (isGuestLikeComment(comment)) {
      setPendingCommentAction('delete');
      setPendingCommentTarget(comment);
      setCommentPasswordModalOpened(true);
      return;
    }

    if (comment.isMine) {
      setCommentDeleteTarget(comment);
      setCommentDeleteModalOpened(true);
    }
  };

  const handleConfirmClickCommentPasswordModal = async (password: string) => {
    if (!pendingCommentTarget) {
      return;
    }

    try {
      await postCommunityPostCommentGuestCheck({
        commentId: pendingCommentTarget.id,
        guestPassword: password,
      });

      setCommentPasswordModalOpened(false);

      if (pendingCommentAction === 'edit') {
        setEditingComment(pendingCommentTarget);
        setEditingCommentGuestPassword(password);
        setPendingCommentAction(null);
        setPendingCommentTarget(null);
        return;
      }

      setCommentDeleteTarget(pendingCommentTarget);
      setCommentDeletePassword(password);
      setCommentDeleteModalOpened(true);
      setPendingCommentAction(null);
      setPendingCommentTarget(null);
    } catch {
      alert('비밀번호가 일치하지 않습니다.');
    }
  };

  const handleConfirmClickCommentDeleteModal = () => {
    if (!commentDeleteTarget) {
      return;
    }

    deleteCommentMutate({
      commentId: commentDeleteTarget.id,
      ...(commentDeleteTarget.isGuestComment || commentDeleteTarget.isAnonymous
        ? { guestPassword: commentDeletePassword }
        : {}),
    });
  };

  const handleConfirmClickDeleteModal = () => {
    deletePostMutate({
      postId: postId!,
      ...(shouldRequirePassword
        ? { guestPassword: pendingDeletePassword }
        : {}),
    });
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
          liked={postData.liked}
          isLikePending={isPostLikePending}
          onLikeClick={() => togglePostLikeMutate()}
        />
        <PostCommentSection
          postId={postId ?? ''}
          onReplyClick={handleReplyClick}
          onEditClick={handleEditCommentClick}
          onDeleteClick={handleDeleteCommentClick}
        />
      </S_Content>
      <InputSection
        postId={postId ?? ''}
        authenticated={authenticated}
        replyTarget={replyTarget}
        editingComment={editingComment}
        editingCommentGuestPassword={editingCommentGuestPassword}
        onCancelReply={() => setReplyTarget(null)}
        onCancelEdit={() => {
          setEditingComment(null);
          setEditingCommentGuestPassword('');
        }}
        onSubmitSuccess={() => setReplyTarget(null)}
        onSubmitEditSuccess={() => {
          setEditingComment(null);
          setEditingCommentGuestPassword('');
        }}
      />
      <CommunityPostPasswordModal
        opened={passwordModalOpened}
        onCloseClick={handleCloseClickPasswordModal}
        onConfirmClick={handleConfirmClickPasswordModal}
        title="비밀번호 확인"
        description={`비회원 게시글을 ${pendingAction === 'delete' ? '삭제' : '수정'}하려면 비밀번호를 입력해주세요.`}
        confirmLabel={pendingAction === 'delete' ? '삭제하기' : '수정하기'}
      />
      <DeleteConfirmModal
        opened={deleteModalOpened}
        onCloseClick={handleCloseClickDeleteModal}
        onConfirmClick={handleConfirmClickDeleteModal}
        title="게시글을 삭제하시겠습니까?"
        description="삭제한 게시글은 다시 복구할 수 없습니다."
      />
      <CommunityPostPasswordModal
        opened={commentPasswordModalOpened}
        onCloseClick={handleCloseClickCommentPasswordModal}
        onConfirmClick={handleConfirmClickCommentPasswordModal}
        title="댓글 비밀번호 확인"
        description="댓글을 수정하거나 삭제하려면 비밀번호를 입력해주세요."
        confirmLabel={pendingCommentAction === 'delete' ? '삭제하기' : '수정하기'}
      />
      <DeleteConfirmModal
        opened={commentDeleteModalOpened}
        onCloseClick={handleCloseClickCommentDeleteModal}
        onConfirmClick={handleConfirmClickCommentDeleteModal}
        title="댓글을 삭제하시겠습니까?"
        description="삭제한 댓글은 다시 복구할 수 없습니다."
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

  padding-bottom: calc(12rem + env(safe-area-inset-bottom));
`;
