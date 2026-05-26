import { useQuery } from '@tanstack/react-query';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { getMentoringApplicationList } from '../apis/getMentoringApplicationList';

const QUERY_KEY = ['mentoringApplicationList'] as const;

const useMentoringApplicationList = () => {
  const {
    data: mentoringApplicationList = [],
    error,
    refetch: refetchMentoringApplicationList,
  } = useQuery({
    queryKey: QUERY_KEY,
    queryFn: getMentoringApplicationList,
  });

  if (error) {
    captureSentryError({
      error,
      level: 'warning',
      feature: 'createdMentoring',
      step: 'mentoring-application-fetch',
    });
  }

  return { mentoringApplicationList, refetchMentoringApplicationList };
};

export default useMentoringApplicationList;
