import { useState } from 'react';

export type SortKey = 'CREATED_AT' | 'RESERVATION_COUNT' | 'AVERAGE_RATING';

const useSortKey = () => {
  const [sortKey, setSortKey] = useState<SortKey>('CREATED_AT');

  const changeSortKey = (newOption: SortKey) => {
    setSortKey(newOption);
  };

  return {
    sortKey,
    changeSortKey,
  };
};

export default useSortKey;
