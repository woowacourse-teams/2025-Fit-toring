import styled from '@emotion/styled';

import CommunityContent from './components/CommunityContent/CommunityContent';
import CommunityHeader from './components/CommunityHeader/CommunityHeader';
import WriteButton from './components/WriteButton/WriteButton';

function Community() {
  return (
    <S_Container>
      <CommunityHeader />
      <CommunityContent />
      <WriteButton label="글쓰기" />
    </S_Container>
  );
}

export default Community;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  position: relative;

  min-height: 100%;
`;
