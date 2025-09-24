import { useState, useEffect } from 'react';

import { getMineMentoring } from '../../../common/apis/getMineMentoring';
import { captureSentryError } from '../../../common/utils/captureSentryError';

import type { MentoringDetail } from '../../../common/types/MentoringDetail';

const useMineMentoring = () => {
  const [mineMentoring, setMineMentoring] = useState<MentoringDetail | null>(
    null,
  );
  useEffect(() => {
    const fetchMentoring = async () => {
      try {
        const mentoring = await getMineMentoring();
        setMineMentoring(mentoring);
      } catch (error) {
        console.error(error);
        captureSentryError({
          error,
          level: 'warning',
          feature: 'createdMentoring',
          step: 'mine-mentoring-fetch',
        });
      }
    };

    fetchMentoring();
  }, []);

  return { mineMentoring };
};

export default useMineMentoring;
