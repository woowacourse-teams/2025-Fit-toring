import type { MouseEvent } from 'react';

import styled from '@emotion/styled';
import { Link } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import { formatTimeAgo } from '../../../../common/utils/formatTimeAgo';
import { isPlainPrimaryClick } from '../../../../common/utils/isPlainPrimaryClick';
import { saveCommunityScrollY } from '../../utils/communityScrollStorage';
import ReactionCount from '../ReactionCount/ReactionCount';

import type { CommunityPost } from '../../../../common/types/communityPost';

interface CommunityPostCardProps {
  post: CommunityPost;
}

function CommunityPostCard({ post }: CommunityPostCardProps) {
  const {
    id,
    title,
    nickname,
    createdAt,
    viewCount,
    likeCount,
    commentCount,
    content,
  } = post;
  const createdAtLabel = formatTimeAgo(createdAt);
  const authorLabel = nickname;

  const handleLinkClick = (event: MouseEvent<HTMLAnchorElement>) => {
    if (!isPlainPrimaryClick(event)) {
      return;
    }

    saveCommunityScrollY(event.currentTarget);
  };

  return (
    <S_ListItem>
      <S_Link to={`${PAGE_URL.COMMUNITY}/${id}`} onClick={handleLinkClick}>
        <S_Card>
          <S_TextBlock>
            <S_Title>{title}</S_Title>
          </S_TextBlock>
          <S_Content>{content}</S_Content>

          <S_FooterRow>
            <S_MetaText
              aria-label={`${authorLabel} 작성, ${createdAtLabel}, 조회 ${viewCount.toLocaleString()}`}
            >
              <S_Author>{authorLabel}</S_Author>
              <S_MetaSeparator aria-hidden="true">|</S_MetaSeparator>
              <S_MetaDetail>
                {createdAtLabel} · 조회 {viewCount.toLocaleString()}
              </S_MetaDetail>
            </S_MetaText>
            <ReactionCount likeCount={likeCount} commentCount={commentCount} />
          </S_FooterRow>
        </S_Card>
      </S_Link>
    </S_ListItem>
  );
}

export default CommunityPostCard;

const S_ListItem = styled.li`
  list-style: none;
`;

const S_Link = styled(Link)`
  display: block;

  color: inherit;
  text-decoration: none;
`;

const S_Card = styled.article`
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  position: relative;

  padding: 1.6rem;

  &::after {
    content: '';

    position: absolute;
    right: 1.6rem;
    bottom: 0;
    left: 1.6rem;

    height: 1px;

    background-color: ${({ theme }) => theme.OUTLINE.LIGHT};
  }
`;

const S_TextBlock = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.8rem;

  min-width: 0;
`;

const S_Title = styled.h2`
  overflow: hidden;

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_SB};
  line-height: 1.35;
  text-overflow: ellipsis;

  white-space: nowrap;
`;

const S_Content = styled.p`
  overflow: hidden;

  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
  line-height: 1.45;
  text-overflow: ellipsis;

  white-space: nowrap;
`;

const S_FooterRow = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.2rem;

  min-width: 0;
`;

const S_MetaText = styled.div`
  display: flex;
  align-items: center;
  gap: 0.6rem;
  overflow: hidden;

  min-width: 0;

  color: ${({ theme }) => theme.SYSTEM.GRAY600};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  line-height: 1.2;
  white-space: nowrap;
`;

const S_Author = styled.span`
  display: inline-flex;
  flex-shrink: 0;
  overflow: hidden;

  max-width: 10rem;

  color: ${({ theme }) => theme.SYSTEM.GRAY600};

  text-overflow: ellipsis;
  ${({ theme }) => theme.TYPOGRAPHY.B4_SB};
`;

const S_MetaSeparator = styled.span`
  flex-shrink: 0;

  color: ${({ theme }) => theme.SYSTEM.GRAY300};
`;

const S_MetaDetail = styled.span`
  overflow: hidden;

  text-overflow: ellipsis;
`;
