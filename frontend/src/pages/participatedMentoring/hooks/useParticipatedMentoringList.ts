import { useEffect } from 'react';

import { useQuery } from '@tanstack/react-query';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { getParticipatedMentoringList } from '../apis/getParticipatedMentoring';

export const PARTICIPATED_MENTORING_QUERY_KEY = [
  'participatedMentoringList',
] as const;

const useParticipatedMentoringList = () => {
  const {
    data: participatedMentoringList = [],
    error,
    refetch: refetchParticipatedMentoringList,
  } = useQuery({
    queryKey: PARTICIPATED_MENTORING_QUERY_KEY,
    queryFn: getParticipatedMentoringList,
  });

  useEffect(() => {
    if (!error) {
      return;
    }

    console.error('참여한 멘토링 목록 불러오기 실패:', error);
    captureSentryError({
      error,
      level: 'warning',
      feature: 'participatedMentoring',
      step: 'fetch-participated-mentoring-list',
    });
  }, [error]);

  return {
    participatedMentoringList,
    refetchParticipatedMentoringList,
  };
};

export default useParticipatedMentoringList;
