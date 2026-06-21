import {
  useInfiniteQuery,
  type InfiniteData,
  type QueryKey,
} from '@tanstack/react-query';

import { getMentorListByPage } from '../apis/getMentorListByPage';

import type { SortKey } from './useSortKey';
import type { Specialty } from '../../../common/types/Specialty';
import type { MentoringByPage } from '../types/MentoringByPage';

interface MentorListPageParam {
  cursorCode?: string | null;
}

interface UseInfiniteMentorListParams {
  selectedSpecialties: Specialty[];
  sortKey: SortKey;
}

const convertSelectedSpecialtiesToParams = (
  selectedSpecialties: Specialty[],
): Record<string, string> => {
  const params: Record<string, string> = {};
  params['categoryIds'] = selectedSpecialties.map(({ id }) => id).join(',');

  return params;
};

const useInfiniteMentorList = ({
  selectedSpecialties,
  sortKey,
}: UseInfiniteMentorListParams) => {
  return useInfiniteQuery<
    MentoringByPage,
    Error,
    InfiniteData<MentoringByPage>,
    QueryKey,
    MentorListPageParam
  >({
    queryKey: ['mentorList', selectedSpecialties, sortKey],
    queryFn: ({ pageParam }) =>
      getMentorListByPage({
        params: {
          ...convertSelectedSpecialtiesToParams(selectedSpecialties),
          sortKey,
          ...(pageParam.cursorCode && { cursorCode: pageParam.cursorCode }),
        },
      }),
    initialPageParam: {},
    getNextPageParam: (lastPage) => {
      if (!lastPage.hasNext) {
        return undefined;
      }

      return {
        cursorCode: lastPage.nextCursorCode,
      };
    },
  });
};

export default useInfiniteMentorList;
