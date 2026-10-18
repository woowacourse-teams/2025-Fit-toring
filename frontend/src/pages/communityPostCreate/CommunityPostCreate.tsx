import SEO from '../../common/components/SEO/SEO';
import { PAGE_URL } from '../../common/constants/url';

import CommunityPostCreateForm from './components/CommunityPostCreateForm/CommunityPostCreateForm';
import CommunityPostCreateHeader from './components/CommunityPostCreateHeader/CommunityPostCreateHeader';

function CommunityPostCreate() {
  return (
    <div>
      <SEO
        title="게시글 작성 - 핏토링 Fittoring"
        canonicalPath={PAGE_URL.COMMUNITY_CREATE}
      />
      <CommunityPostCreateHeader />
      <CommunityPostCreateForm />
    </div>
  );
}

export default CommunityPostCreate;
