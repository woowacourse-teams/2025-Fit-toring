import { useQuery } from '@tanstack/react-query';

import { getMineMentoring } from '../../../common/apis/getMineMentoring';
import { captureSentryError } from '../../../common/utils/captureSentryError';

const useMineMentoring = () => {
  const { data: mineMentoring, error } = useQuery({
    queryKey: ['mineMentoring'],
    queryFn: getMineMentoring,
  });

  if (error) {
    console.error(error);
    captureSentryError({
      error,
      level: 'warning',
      feature: 'createdMentoring',
      step: 'mine-mentoring-fetch',
    });
  }

  return { mineMentoring };
};

export default useMineMentoring;
