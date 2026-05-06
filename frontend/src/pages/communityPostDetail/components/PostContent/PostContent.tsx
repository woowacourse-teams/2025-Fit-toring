import styled from '@emotion/styled';

import LikeToggleButton from '../../../../common/components/LikeToggleButton/LikeToggleButton';

interface PostContentProps {
  title: string;
  content: string;
  likeCount: number;
  liked: boolean;
  isLikePending?: boolean;
  onLikeClick: () => void;
}

function PostContent({
  title,
  content,
  likeCount,
  liked,
  isLikePending = false,
  onLikeClick,
}: PostContentProps) {
  return (
    <S_Container>
      <S_Title>{title}</S_Title>
      <S_Content>{content}</S_Content>
      <LikeToggleButton
        count={likeCount}
        pressed={liked}
        size="medium"
        ariaLabel={`좋아요 ${likeCount}개`}
        disabled={isLikePending}
        onClick={onLikeClick}
      />
    </S_Container>
  );
}

export default PostContent;

const S_Container = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1.6rem;

  padding: 2.4rem 2rem 3.2rem;
  border-bottom: 0.8rem solid ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  white-space: pre-wrap;
  ${({ theme }) => theme.TYPOGRAPHY.H4_SB}
`;

const S_Content = styled.p`
  color: ${({ theme }) => theme.FONT.B02};
  white-space: pre-wrap;
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
