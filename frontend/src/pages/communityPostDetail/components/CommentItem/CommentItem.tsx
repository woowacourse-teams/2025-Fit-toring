import styled from '@emotion/styled';

import { formatTimeAgo } from '../../../../common/utils/formatTimeAgo';

import type { PostComment } from '../../types/postComment';

interface CommentItemProps {
  comment: PostComment;
}

function CommentItem({ comment }: CommentItemProps) {
  return (
    <S_Container>
      <S_Header>
        <S_Nickname>{comment.nickname}</S_Nickname>
        <S_CreatedAt>{formatTimeAgo(comment.createdAt)}</S_CreatedAt>
      </S_Header>
      <S_Content>{comment.content}</S_Content>
    </S_Container>
  );
}

export default CommentItem;

const S_Container = styled.li`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  padding: 1.6rem 0;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.LIGHT};
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
