import { useCallback, useState } from 'react';
import type { ChangeEvent } from 'react';

const useImageFile = () => {
  const [selectedImage, setSelectedImage] = useState<File | null>(null);

  const handleImageChange = useCallback((e: ChangeEvent<HTMLInputElement>) => {
    if (!e.target.files || e.target.files.length === 0) {
      setSelectedImage(null);
      return;
    }

    const file = e.target.files[0];

    setSelectedImage(file);
  }, []);

  const cancelImageSelection = useCallback(() => {
    setSelectedImage(null);
  }, []);

  return {
    selectedImage,
    handleImageChange,
    cancelImageSelection,
  };
};

export default useImageFile;
