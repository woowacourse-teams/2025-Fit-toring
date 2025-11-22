import { useState } from 'react';

const useTabs = <T>(initialState?: T) => {
  const [selectedTab, setSelected] = useState(initialState);

  const selectTab = (tab: T) => {
    setSelected(tab);
  };

  return { selectedTab, selectTab };
};

export default useTabs;
