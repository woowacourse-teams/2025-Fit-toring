import type { ReactNode } from 'react';

import styled from '@emotion/styled';

import { formatTimeAgo } from '../../../../common/utils/formatTimeAgo';

import type { PostComment } from '../../types/postComment';

interface CommentItemProps {
  comment: PostComment;
  depth: number;
  onReplyClick: (comment: PostComment) => void;
  children?: ReactNode;
}

function CommentItem({
  comment,
  depth,
  onReplyClick,
  children,
}: CommentItemProps) {
  return (
    <S_Container depth={depth}>
      <S_Header>
        <S_Nickname>{comment.isAnonymous ? '익명' : comment.nickname}</S_Nickname>
        <S_CreatedAt>{formatTimeAgo(comment.createdAt)}</S_CreatedAt>
      </S_Header>
      <S_Content>
        {comment.isDeleted ? '삭제된 댓글입니다.' : comment.content}
      </S_Content>
      {!comment.isDeleted ? (
        <S_Actions>
          <S_ReplyButton type="button" onClick={() => onReplyClick(comment)}>
            답글쓰기
          </S_ReplyButton>
        </S_Actions>
      ) : null}
      {children}
    </S_Container>
  );
}

export default CommentItem;

const S_Container = styled.li<{ depth: number }>`
  display: flex;
  flex-direction: column;
  gap: 0.6rem;

  padding: 1.2rem 0 1.2rem ${({ depth }) => `${depth * 1.6}rem`};
`;

const S_Header = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.2rem;
`;

const S_Nickname = styled.strong`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B3_SB}
`;

const S_CreatedAt = styled.span`
  flex-shrink: 0;

  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const S_Content = styled.p`
  color: ${({ theme }) => theme.FONT.B02};
  white-space: pre-wrap;
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_Actions = styled.div`
  display: flex;
  justify-content: flex-start;
`;

const S_ReplyButton = styled.button`
  padding: 0;
  border: none;

  background: transparent;

  color: ${({ theme }) => theme.FONT.B04};
  cursor: pointer;
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;
