import { useQuery } from '@tanstack/react-query';

import { getMineMentoring } from '../../../common/apis/getMineMentoring';

export const QUERY_KEY = {
  myMentoringId: (key: string) => ['myMentoringId', key],
} as const;

const useMyMentoringId = (authenticated: boolean) => {
  const storedData = localStorage.getItem('memberId');
  const memberId = storedData ? JSON.parse(storedData) : null;

  const { data: myMentoringId = null, error } = useQuery({
    queryKey: QUERY_KEY.myMentoringId(memberId),
    queryFn: getMineMentoring,
    select: (data) => data.id,
    enabled: authenticated,
    retry: 1,
  });

  if (error) {
    console.error(error);
  }

  return { myMentoringId };
};

export default useMyMentoringId;
