import { useState } from 'react';

const useShowMore = () => {
  const [showMore, setShowMore] = useState(false);

  const toggleShowMore = () => {
    setShowMore((prev) => !prev);
  };

  return { showMore, toggleShowMore };
};

export default useShowMore;
