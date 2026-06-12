import { useState } from 'react';
import type { ReactNode } from 'react';

import styled from '@emotion/styled';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import { getCommunityPostCommentOwnership } from '../../apis/getCommunityPostCommentOwnership';
import { getPostComments } from '../../apis/getPostComments';
import {
  deleteCommunityPostCommentLike,
  postCommunityPostCommentLike,
} from '../../apis/postCommunityPostCommentLike';
import { buildCommentTree } from '../../utils/buildCommentTree';
import CommentItem from '../CommentItem/CommentItem';

import type { PostComment } from '../../types/postComment';

interface PostCommentSectionProps {
  postId: string;
  authenticated: boolean;
  onReplyClick: (comment: PostComment) => void;
  onEditClick: (comment: PostComment) => void;
  onDeleteClick: (comment: PostComment) => void;
}

interface PostCommentNode extends PostComment {
  children: PostCommentNode[];
}

function PostCommentSection({
  postId,
  authenticated,
  onReplyClick,
  onEditClick,
  onDeleteClick,
}: PostCommentSectionProps) {
  const queryClient = useQueryClient();
  const [pendingCommentIds, setPendingCommentIds] = useState<Set<number>>(
    new Set(),
  );
  const memberId = localStorage.getItem('memberId');
  const {
    data: commentData = [],
    isPending,
    isError,
  } = useQuery({
    queryKey: ['postComments', postId],
    queryFn: () => getPostComments(postId),
    enabled: Boolean(postId),
  });

  const { mutate: mutateCommentLike } = useMutation({
    mutationFn: async (comment: PostComment) => {
      if (comment.liked) {
        return await deleteCommunityPostCommentLike({
          postId,
          commentId: comment.id,
        });
      }

      return await postCommunityPostCommentLike({
        postId,
        commentId: comment.id,
      });
    },
    onMutate: (comment) => {
      setPendingCommentIds((prev) => {
        const next = new Set(prev);
        next.add(comment.id);
        return next;
      });
    },
    onSettled: (_, __, comment) => {
      setPendingCommentIds((prev) => {
        const next = new Set(prev);
        next.delete(comment.id);
        return next;
      });
    },
    onSuccess: (updatedComment) => {
      queryClient.setQueryData<PostComment[]>(
        ['postComments', postId],
        (currentComments) =>
          currentComments?.map((comment) =>
            comment.id === updatedComment.commentId
              ? {
                  ...comment,
                  liked: updatedComment.liked,
                  likeCount: updatedComment.likeCount,
                }
              : comment,
          ) ?? currentComments,
      );
    },
    onError: () => {
      alert('댓글 좋아요에 실패했습니다.');
    },
  });

  const { data: commentOwnershipData } = useQuery({
    queryKey: ['communityPostCommentOwnership', postId, memberId],
    queryFn: () => getCommunityPostCommentOwnership(postId),
    enabled: Boolean(postId && authenticated),
    retry: false,
  });

  const mineCommentIds = new Set(commentOwnershipData?.mineCommentIds ?? []);
  const comments: PostComment[] = commentData.map((comment) => ({
    ...comment,
    isMine: mineCommentIds.has(comment.id),
  }));
  const commentTree = buildCommentTree(comments);

  const handleLikeClick = (comment: PostComment) => {
    mutateCommentLike(comment);
  };

  if (isPending) {
    return (
      <S_Container>
        <S_Title>댓글</S_Title>
        <S_StatusWrapper>
          <LoadingSpinner />
        </S_StatusWrapper>
      </S_Container>
    );
  }

  return (
    <S_Container>
      <S_Title>댓글 {comments.length}개</S_Title>
      {isError ? (
        <S_StatusText>댓글을 불러오지 못했습니다.</S_StatusText>
      ) : null}
      {!isPending && !isError && comments.length === 0 ? (
        <S_StatusText>첫 댓글을 남겨 보세요.</S_StatusText>
      ) : null}
      {!isPending && !isError && commentTree.length > 0 ? (
        <S_CommentList>
          {commentTree.flatMap((comment) =>
            renderCommentNode(
              comment,
              onReplyClick,
              onEditClick,
              onDeleteClick,
              handleLikeClick,
              pendingCommentIds,
            ),
          )}
        </S_CommentList>
      ) : null}
    </S_Container>
  );
}

export default PostCommentSection;

function renderCommentNode(
  comment: PostCommentNode,
  onReplyClick: (comment: PostComment) => void,
  onEditClick: (comment: PostComment) => void,
  onDeleteClick: (comment: PostComment) => void,
  onLikeClick: (comment: PostComment) => void,
  pendingCommentIds: Set<number>,
  depth = 0,
): ReactNode[] {
  const isLikePending = pendingCommentIds.has(comment.id);

  return [
    <CommentItem
      key={comment.id}
      comment={comment}
      depth={depth}
      onReplyClick={onReplyClick}
      onEditClick={onEditClick}
      onDeleteClick={onDeleteClick}
      onLikeClick={onLikeClick}
      isLikePending={isLikePending}
    />,
    ...comment.children.flatMap((childComment) =>
      renderCommentNode(
        childComment,
        onReplyClick,
        onEditClick,
        onDeleteClick,
        onLikeClick,
        pendingCommentIds,
        depth + 1,
      ),
    ),
  ];
}

const S_Container = styled.section`
  display: flex;
  flex: 1;
  flex-direction: column;

  padding: 2.4rem 2rem 0;
`;

const S_Title = styled.h3`
  padding-bottom: 1.6rem;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_SB}
`;

const S_StatusWrapper = styled.div`
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;

  min-height: 16rem;
  padding: 3.2rem 0;
`;

const S_StatusText = styled.p`
  padding: 3.2rem 0;

  color: ${({ theme }) => theme.FONT.B04};
  text-align: center;
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_CommentList = styled.ul`
  display: flex;
  flex-direction: column;
`;
