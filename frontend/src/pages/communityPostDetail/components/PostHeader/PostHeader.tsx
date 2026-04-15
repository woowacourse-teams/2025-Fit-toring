import styled from '@emotion/styled';

import defaultProfileImg from '../../../../common/assets/images/profileImg.svg';
import { formatTimeAgo } from '../../../../common/utils/formatTimeAgo';

interface PostHeaderProps {
  nickname: string;
  createdAt: string;
  viewCount: number;
  profileImageUrl?: string | null;
}

function PostHeader({
  nickname,
  createdAt,
  viewCount,
  profileImageUrl,
}: PostHeaderProps) {
  return (
    <S_Container>
      <S_ProfileImage
        src={profileImageUrl || defaultProfileImg}
        alt="프로필사진"
      />
      <S_TextSection>
        <S_Nickname>{nickname}</S_Nickname>
        <S_MetaData>
          <S_MetaText>조회수 {viewCount}</S_MetaText>
          <S_Dot />
          <S_MetaText>{formatTimeAgo(createdAt)}</S_MetaText>
        </S_MetaData>
      </S_TextSection>
    </S_Container>
  );
}

export default PostHeader;

const S_Container = styled.section`
  display: flex;
  align-items: center;
  gap: 1.2rem;

  padding: 2rem 2rem 1.6rem;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;

const S_ProfileImage = styled.img`
  flex-shrink: 0;

  width: 5rem;
  height: 5rem;
  border-radius: 50%;

  object-fit: cover;
`;

const S_TextSection = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.6rem;
`;

const S_Nickname = styled.strong`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_SB}
`;

const S_MetaData = styled.div`
  display: flex;
  align-items: center;
  gap: 0.8rem;
`;

const S_MetaText = styled.span`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
`;

const S_Dot = styled.span`
  width: 0.3rem;
  height: 0.3rem;
  border-radius: 50%;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY300};
`;
