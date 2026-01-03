import { useQuery } from '@tanstack/react-query';

import { getMineMentoring } from '../../../common/apis/getMineMentoring';

export const QUERY_KEY = {
  myMentoringId: (key: string | null) => ['myMentoringId', key],
} as const;

const useMyMentoringId = (authenticated: boolean) => {
  const memberId = localStorage.getItem('memberId');
  const enabled = authenticated && !!memberId;

  const { data: myMentoringId = null, error } = useQuery({
    queryKey: QUERY_KEY.myMentoringId(memberId),
    queryFn: getMineMentoring,
    select: (data) => data.id,
    enabled,
    retry: 1,
  });

  if (error) {
    console.error(error);
  }

  return {
    myMentoringId: enabled ? (myMentoringId ?? null) : null,
  };
};

export default useMyMentoringId;
