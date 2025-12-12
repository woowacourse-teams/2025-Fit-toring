import { useQuery, useQueryClient } from '@tanstack/react-query';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { getMentoringApplicationList } from '../apis/getMentoringApplicationList';

const QUERY_KEY = ['mentoringApplicationList'] as const;

const useMentoringApplicationList = () => {
  const queryClient = useQueryClient();

  const { data: mentoringApplicationList = [], error } = useQuery({
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

  const refetchMentoringApplicationList = async () => {
    await queryClient.invalidateQueries({ queryKey: QUERY_KEY });
  };

  return { mentoringApplicationList, refetchMentoringApplicationList };
};

export default useMentoringApplicationList;
