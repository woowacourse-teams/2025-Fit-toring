import { Helmet } from 'react-helmet-async';

import { SEO as SEO_CONSTANTS } from '../../constants/seo';

interface SEOProps {
  title?: string;
  description?: string;
  canonicalPath?: string;
  imageUrl?: string | null;
  type?: 'website' | 'article' | 'profile';
}

function SEO({
  title = SEO_CONSTANTS.DEFAULT_TITLE,
  description = SEO_CONSTANTS.DEFAULT_DESCRIPTION,
  canonicalPath = '/',
  imageUrl = SEO_CONSTANTS.DEFAULT_IMAGE_URL,
  type = 'website',
}: SEOProps) {
  const canonicalUrl = new URL(canonicalPath, SEO_CONSTANTS.SITE_URL).toString();
  const ogImageUrl = imageUrl || SEO_CONSTANTS.DEFAULT_IMAGE_URL;

  return (
    <Helmet>
      <title>{title}</title>
      <meta name="description" content={description} />
      <link rel="canonical" href={canonicalUrl} />

      <meta property="og:title" content={title} />
      <meta property="og:description" content={description} />
      <meta property="og:image" content={ogImageUrl} />
      <meta property="og:url" content={canonicalUrl} />
      <meta property="og:type" content={type} />
    </Helmet>
  );
}

export default SEO;
