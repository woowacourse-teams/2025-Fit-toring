import { useParams } from 'react-router-dom';

import SEO from '../../common/components/SEO/SEO';
import { PAGE_URL } from '../../common/constants/url';

import CommunityPostUpdateForm from './components/CommunityPostUpdateForm/CommunityPostUpdateForm';
import CommunityPostUpdateHeader from './components/CommunityPostUpdateHeader/CommunityPostUpdateHeader';

function CommunityPostUpdate() {
  const { postId } = useParams();

  return (
    <div>
      <SEO
        title="게시글 수정 - 핏토링 Fittoring"
        canonicalPath={`${PAGE_URL.COMMUNITY}/${postId}${PAGE_URL.EDIT}`}
      />
      <CommunityPostUpdateHeader />
      <CommunityPostUpdateForm />
    </div>
  );
}

export default CommunityPostUpdate;
