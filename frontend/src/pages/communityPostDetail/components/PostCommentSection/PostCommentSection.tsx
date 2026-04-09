import styled from '@emotion/styled';

import LoadingSpinner from '../../../../common/components/LoadingSpinner/LoadingSpinner';
import CommentItem from '../CommentItem/CommentItem';

interface PostCommentSectionProps {
  postId: string;
}

function PostCommentSection({ postId }: PostCommentSectionProps) {
  const commentData = [
    {
      id: 201,
      content: '루트 댓글',
      nickname: 'user1',
      isAnonymous: false,
      isGuestComment: false,
      rootId: null,
      parentId: null,
      isDeleted: false,
      createdAt: '2026-04-06T21:30:00',
    },
    {
      id: 202,
      content: '대댓글',
      nickname: 'user2',
      isAnonymous: false,
      isGuestComment: true,
      rootId: 201,
      parentId: 201,
      isDeleted: false,
      createdAt: '2026-04-06T21:31:00',
    },
  ];

  const isPending = false;
  const isError = false;

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
      {!isPending && !isError && commentData.length > 0 ? (
        <S_CommentList>
          {commentData.map((comment) => (
            <CommentItem key={comment.id} comment={comment} />
          ))}
        </S_CommentList>
      ) : null}
    </S_Container>
  );
}

export default PostCommentSection;

const S_Container = styled.section`
  display: flex;
  flex: 1;
  flex-direction: column;

  padding: 2.4rem 2rem 3.2rem;
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
