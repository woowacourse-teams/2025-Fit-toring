import { useEffect, useState } from 'react';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { getMentoringApplicationList } from '../apis/getMentoringApplicationList';

import type { StatusType } from '../../../common/types/statusType';
import type { MentoringApplication } from '../types/mentoringApplication';

const useMentoringApplicationList = () => {
  const [mentoringApplicationList, setMentoringApplicationList] = useState<
    MentoringApplication[]
  >([]);

  useEffect(() => {
    const fetchMentoringApplicationList = async () => {
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
    };

    fetchMentoringApplicationList();
  }, []);

  const updateMentoringApplicationListStatus = ({
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
  };

  return { mentoringApplicationList, updateMentoringApplicationListStatus };
};

export default useMentoringApplicationList;
