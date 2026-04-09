import styled from '@emotion/styled';

import CommunityFeed from '../CommunityFeed/CommunityFeed';

import type { CommunityPost } from '../../types/posts';

const communityPosts: CommunityPost[] = [
  {
    id: 9,
    title: '동네에서 시간 될 때 만날 친구 구해요',
    nickname: '작성자A',
    isAnonymous: false,
    createdAt: '2026-04-09T16:30:00',
    commentCount: 0,
    viewCount: 140,
    likeCount: 0,
    content:
      '동네에서 시간되고 여유될 때 운동도 같이하고 수다도 떨고 맛있는 것도 먹으면서 편하게 지낼 친구 구해요',
  },
  {
    id: 10,
    title: '어제 버터떡 글 어디갔나요..?',
    nickname: '작성자B',
    isAnonymous: true,
    createdAt: '2026-04-06T20:00:00',
    commentCount: 3,
    viewCount: 20,
    likeCount: 4,
    content: '9시에 구매한다고 했는데 글이 사라졌어요..',
  },
  {
    id: 11,
    title: '오 유퀴즈에 나오셨던 분인데, 광명에 오시나봐요~~ㅎ',
    nickname: '작성자C',
    isAnonymous: false,
    createdAt: '2026-04-03T18:10:00',
    commentCount: 2,
    viewCount: 2696,
    likeCount: 4,
    content: '행사 일정 올라온 것 같은데 관심 있는 분들 같이 가요.',
  },
  {
    id: 12,
    title: '친구처럼 지낼 친구 구하고 있습니다!',
    nickname: '작성자D',
    isAnonymous: false,
    createdAt: '2026-04-02T14:20:00',
    commentCount: 0,
    viewCount: 98,
    likeCount: 0,
    content:
      '22살 이고 INFJ입니다! 고민도 들어주고 취미도 공유하면서 가볍게 연락할 분 구해요',
  },
];

function CommunityContent() {
  return (
    <S_Container>
      <CommunityFeed posts={communityPosts} />
    </S_Container>
  );
}

export default CommunityContent;

const S_Container = styled.main`
  position: relative;

  min-height: 100%;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;
