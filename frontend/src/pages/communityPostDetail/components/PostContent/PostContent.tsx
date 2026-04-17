import styled from '@emotion/styled';

import thumbsUpIcon from '../../../../common/assets/images/thumbsUpIcon.svg';

interface PostContentProps {
  title: string;
  content: string;
  likeCount: number;
}

function PostContent({ title, content, likeCount }: PostContentProps) {
  return (
    <S_Container>
      <S_Title>{title}</S_Title>
      <S_Content>{content}</S_Content>
      <S_LikeButton type="button" aria-label={`좋아요 ${likeCount}개`}>
        <S_LikeIcon src={thumbsUpIcon} alt="" aria-hidden="true" />
        <S_LikeCount>{likeCount}</S_LikeCount>
      </S_LikeButton>
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

const S_LikeButton = styled.button`
  display: inline-flex;
  align-items: center;
  gap: 0.8rem;
  align-self: flex-start;

  padding: 0.9rem 1.8rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY100};
  border-radius: 999px;

  background-color: ${({ theme }) => theme.BG.WHITE};
  cursor: pointer;
`;

const S_LikeIcon = styled.img`
  width: 1.8rem;
  height: 1.8rem;
`;

const S_LikeCount = styled.span`
  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
