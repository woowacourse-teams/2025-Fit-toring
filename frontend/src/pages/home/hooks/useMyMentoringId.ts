import { useEffect, useState } from 'react';

import { getMineMentoring } from '../../../common/apis/getMineMentoring';

const useMyMentoringId = (authenticated: boolean) => {
  const [myMentoringId, setMyMentoringId] = useState<null | number>(null);

  useEffect(() => {
    if (!authenticated) {
      setMyMentoringId(null);
      return;
    }

    const fetchData = async () => {
      try {
        const response = await getMineMentoring();
        setMyMentoringId(response.id);
      } catch (error) {
        console.error(error);
        setMyMentoringId(null);
      }
    };

    fetchData();
  }, [authenticated]);

  return { myMentoringId };
};

export default useMyMentoringId;
