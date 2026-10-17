import { SEO as SEO_CONSTANTS } from '../../../common/constants/seo';
import { PAGE_URL } from '../../../common/constants/url';

import type { CommunityPostDetail } from '../../../common/types/communityPost';

const DESCRIPTION_MAX_LENGTH = 150;

export const buildCommunityPostSEO = (
  post: Pick<CommunityPostDetail, 'title' | 'content'>,
  postId: string,
) => {
  const title = post.title.replace(/\s+/g, ' ').trim() || '게시글 상세';
  const content = post.content.replace(/\s+/g, ' ').trim();
  const description = content || SEO_CONSTANTS.DEFAULT_DESCRIPTION;

  return {
    title: `${title} - ${SEO_CONSTANTS.SITE_NAME}`,
    description:
      description.length > DESCRIPTION_MAX_LENGTH
        ? `${description.slice(0, DESCRIPTION_MAX_LENGTH - 3)}...`
        : description,
    canonicalPath: `${PAGE_URL.COMMUNITY}/${postId}`,
    type: 'article' as const,
  };
};
