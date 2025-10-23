import { useCallback, useEffect, useState } from 'react';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { getMentoringApplicationList } from '../apis/getMentoringApplicationList';

import type { StatusType } from '../../../common/types/statusType';
import type { MentoringApplication } from '../types/mentoringApplication';

const useMentoringApplicationList = () => {
  const [mentoringApplicationList, setMentoringApplicationList] = useState<
    MentoringApplication[]
  >([]);

  const fetchMentoringApplicationList = useCallback(async () => {
    try {
      const response = await getMentoringApplicationList();
      setMentoringApplicationList(response);
    } catch (error) {
      console.error(error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'createdMentoring',
        step: 'mentoring-application-fetch',
      });
    }
  }, []);

  useEffect(() => {
    fetchMentoringApplicationList();
  }, [fetchMentoringApplicationList]);

  const updateMentoringApplicationListStatus = async ({
    reservationId,
    status,
  }: {
    reservationId: number;
    status: StatusType;
  }) => {
    setMentoringApplicationList((prevList) => {
      return prevList.map((item) => {
        if (item.reservationId !== reservationId) {
          return item;
        }
        return {
          ...item,
          status,
        };
      });
    });

    await fetchMentoringApplicationList();
  };

  return { mentoringApplicationList, updateMentoringApplicationListStatus };
};

export default useMentoringApplicationList;
