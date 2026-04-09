import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import { formatTimeAgo } from '../../../../common/utils/formatTimeAgo';
import ReactionCount from '../ReactionCount/ReactionCount';

import type { CommunityPost } from '../../types/posts';

interface CommunityPostCardProps {
  post: CommunityPost;
}

function CommunityPostCard({ post }: CommunityPostCardProps) {
  const navigate = useNavigate();

  const { id, title, createdAt, viewCount, likeCount, commentCount, content } =
    post;
  const createdAtLabel = formatTimeAgo(createdAt);

  const handlePostItemClick = () => {
    navigate(`${PAGE_URL.COMMUNITY}/${id}`);
  };

  return (
    <S_ListItem onClick={handlePostItemClick}>
      <S_Card>
        <S_TextBlock>
          <S_Title>{title}</S_Title>
          <S_Content>{content}</S_Content>
        </S_TextBlock>

        <S_FooterRow>
          <S_MetaText>
            {createdAtLabel} · 조회 {viewCount.toLocaleString()}
          </S_MetaText>
          <ReactionCount likeCount={likeCount} commentCount={commentCount} />
        </S_FooterRow>
      </S_Card>
    </S_ListItem>
  );
}

export default CommunityPostCard;

const S_ListItem = styled.li`
  list-style: none;
  cursor: pointer;
`;

const S_Card = styled.article`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;

  padding: 1.8rem 1.6rem 1.7rem;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_TextBlock = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
`;

const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_SB};
  line-height: 1.45;
`;

const S_Content = styled.p`
  display: -webkit-box;
  overflow: hidden;

  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
  line-height: 1.45;
  -webkit-box-orient: vertical;
  -webkit-line-clamp: 2;
`;

const S_FooterRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.2rem;
`;

const S_MetaText = styled.p`
  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  line-height: 1.2;
`;
