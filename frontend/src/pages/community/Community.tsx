import styled from '@emotion/styled';

import SEO from '../../common/components/SEO/SEO';
import { PAGE_URL } from '../../common/constants/url';

import CommunityContent from './components/CommunityContent/CommunityContent';
import CommunityHeader from './components/CommunityHeader/CommunityHeader';
import WriteButton from './components/WriteButton/WriteButton';

function Community() {
  return (
    <S_Container>
      <SEO
        title="커뮤니티 - 핏토링 Fittoring"
        canonicalPath={PAGE_URL.COMMUNITY}
      />
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
