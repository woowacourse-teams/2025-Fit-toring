import { PAGE_URL } from '../../../common/constants/url';

import type { MentoringDetail } from '../../../common/types/MentoringDetail';

const DESCRIPTION_MAX_LENGTH = 150;

const normalizeWhitespace = (text: string) => {
  return text.replace(/\s+/g, ' ').trim();
};

const truncate = (text: string, maxLength: number) => {
  if (text.length <= maxLength) {
    return text;
  }

  return `${text.slice(0, maxLength - 3)}...`;
};

const buildDescription = ({
  mentorName,
  categories,
  introduction,
}: Pick<MentoringDetail, 'mentorName' | 'categories' | 'introduction'>) => {
  const categoryText = categories.length > 0 ? `${categories.join(', ')} ` : '';
  const description = `${mentorName} 멘토의 ${categoryText}피트니스 멘토링을 핏토링에서 확인해보세요. ${introduction}`;

  return truncate(normalizeWhitespace(description), DESCRIPTION_MAX_LENGTH);
};

export const buildMentoringDetailSEO = (
  mentoringDetail: MentoringDetail,
  mentoringId: string,
) => {
  return {
    title: `${mentoringDetail.mentorName} 멘토 - 핏토링 Fittoring`,
    description: buildDescription(mentoringDetail),
    canonicalPath: `${PAGE_URL.DETAIL}/${mentoringId}`,
    imageUrl: mentoringDetail.profileImageUrl,
    type: 'profile' as const,
  };
};
