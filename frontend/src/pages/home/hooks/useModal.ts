import { useState } from 'react';

const useModal = () => {
  const [modalOpened, setModalOpened] = useState(false);

  const openModal = () => {
    setModalOpened(true);
  };

  const closeModal = () => {
    setModalOpened(false);
  };

  return {
    modalOpened,
    openModal,
    closeModal,
  };
};

export default useModal;
