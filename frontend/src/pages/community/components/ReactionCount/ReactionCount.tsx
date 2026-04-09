import styled from '@emotion/styled';

import commentIcon from '../../../../common/assets/images/commentIcon.svg';
import thumbsUpIcon from '../../../../common/assets/images/thumbsUpIcon.svg';

interface ReactionCountProps {
  likeCount?: number;
  commentCount?: number;
}

function ReactionCount({ likeCount, commentCount }: ReactionCountProps) {
  if (!likeCount && !commentCount) {
    return null;
  }

  return (
    <S_List aria-label="반응 수">
      {likeCount ? (
        <S_Item>
          <S_ThumbsUpIcon
            aria-hidden="true"
            src={thumbsUpIcon}
            iconHeight="1.35rem"
          />
          <span>{likeCount}</span>
        </S_Item>
      ) : null}
      {commentCount ? (
        <S_Item>
          <S_CommentIcon
            aria-hidden="true"
            src={commentIcon}
            iconHeight="1.35rem"
          />
          <span>{commentCount}</span>
        </S_Item>
      ) : null}
    </S_List>
  );
}

export default ReactionCount;

const S_List = styled.div`
  display: flex;
  flex-shrink: 0;
  align-items: center;
  gap: 1rem;

  color: ${({ theme }) => theme.SYSTEM.GRAY500};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
`;

const S_Item = styled.div`
  display: flex;
  align-items: center;
  gap: 0.35rem;

  line-height: 1;
`;

const S_ThumbsUpIcon = styled.img<{ iconHeight: string }>`
  position: relative;

  height: ${({ iconHeight }) => iconHeight};
  aspect-ratio: 14.88 / 20;
`;

const S_CommentIcon = styled.img<{ iconHeight: string }>`
  position: relative;

  height: ${({ iconHeight }) => iconHeight};
  aspect-ratio: 18 / 16.59;
`;
