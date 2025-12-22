import { useCallback, useState } from 'react';

import { getMentorListByPage } from '../apis/getMentorListByPage';

import type { SortKey } from './useSortKey';
import type { Specialty } from '../../../common/types/Specialty';
import type { MentorInformation } from '../types/MentorInformation';
import type { MentoringByPage } from '../types/MentoringByPage';

const convertSelectedSpecialtiesToParams = (
  selectedSpecialties: Specialty[],
): Record<string, string> => {
  const params: Record<string, string> = {};
  params['categoryIds'] = selectedSpecialties.map(({ id }) => id).join(',');

  return params;
};

const useMentorList = () => {
  const [mentorList, setMentorList] = useState<MentorInformation[]>([]);
  const [hasNext, setHasNext] = useState(true);
  const [cursorCode, setCursorCode] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);

  const getMentors = useCallback(
    async (
      selectedSpecialties: Specialty[],
      sortKey: SortKey,
      cursorCode: string | null = null,
    ) => {
      const data = await getMentorListByPage({
        params: {
          ...convertSelectedSpecialtiesToParams(selectedSpecialties),
          sortKey,
          ...(cursorCode && { cursorCode }),
        },
      });

      return data;
    },
    [],
  );

  const initializeMentorList = useCallback(
    ({
      mentoringSummaryResponses,
      hasNext,
      nextCursorCode,
    }: MentoringByPage) => {
      setMentorList(mentoringSummaryResponses);
      setHasNext(hasNext);
      setCursorCode(nextCursorCode);
    },
    [],
  );

  const fetchInitialMentors = useCallback(
    async (
      selectedSpecialties: Specialty[],
      sortKey: SortKey,
      cursorCode: string | null = null,
    ) => {
      setIsLoading(true);
      const data = await getMentors(selectedSpecialties, sortKey, cursorCode);
      initializeMentorList(data);
      setIsLoading(false);
    },
    [getMentors, initializeMentorList],
  );

  const appendMentorList = useCallback(
    ({
      mentoringSummaryResponses,
      hasNext,
      nextCursorCode,
    }: MentoringByPage) => {
      setMentorList((prev) => [...prev, ...mentoringSummaryResponses]);
      setHasNext(hasNext);
      setCursorCode(nextCursorCode);
    },
    [],
  );

  const fetchMoreMentors = useCallback(
    async (
      selectedSpecialties: Specialty[],
      sortKey: SortKey,
      cursorCode: string | null,
    ) => {
      const data = await getMentors(selectedSpecialties, sortKey, cursorCode);
      appendMentorList(data);
    },
    [appendMentorList, getMentors],
  );

  return {
    mentorList,
    hasNext,
    cursorCode,
    isLoading,
    fetchInitialMentors,
    fetchMoreMentors,
  };
};

export default useMentorList;
