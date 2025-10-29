import { useQuery } from '@tanstack/react-query';

import { getMentoringDetail } from '../../../common/apis/getMentoringDetail';

const useMentoringDetail = (mentoringId: string) => {
  const { data, isLoading, isError, error } = useQuery({
    queryKey: ['mentoringDetail', mentoringId],
    queryFn: () => getMentoringDetail(mentoringId!),
  });

  return { data, isLoading, isError, error };
};

export default useMentoringDetail;
