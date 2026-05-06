import type { ReactNode } from 'react';

import styled from '@emotion/styled';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
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
  onReplyClick: (comment: PostComment) => void;
  onEditClick: (comment: PostComment) => void;
  onDeleteClick: (comment: PostComment) => void;
}

interface PostCommentNode extends PostComment {
  children: PostCommentNode[];
}

function PostCommentSection({
  postId,
  onReplyClick,
  onEditClick,
  onDeleteClick,
}: PostCommentSectionProps) {
  const queryClient = useQueryClient();
  const {
    data: commentData = [],
    isPending,
    isError,
  } = useQuery({
    queryKey: ['postComments', postId],
    queryFn: () => getPostComments(postId),
    enabled: Boolean(postId),
  });

  const { mutate: mutateCommentLike, isPending: isCommentLikePending } =
    useMutation({
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

  const commentTree = buildCommentTree(commentData);

  const handleLikeClick = (comment: PostComment) => {
    mutateCommentLike(comment);
  };

  return (
    <S_Container>
      <S_Title>댓글 {commentData.length}</S_Title>
      {isPending ? (
        <S_StatusWrapper>
          <LoadingSpinner />
        </S_StatusWrapper>
      ) : null}
      {isError ? (
        <S_StatusText>댓글을 불러오지 못했습니다.</S_StatusText>
      ) : null}
      {!isPending && !isError && commentData.length === 0 ? (
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
              isCommentLikePending,
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
  isLikePending: boolean,
  depth = 0,
): ReactNode[] {
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
        isLikePending,
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
  justify-content: center;

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
