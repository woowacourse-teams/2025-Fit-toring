import { useState } from 'react';

const usePreviewImage = (initialCertificate?: string | null) => {
  const [previewUrl, setPreviewUrl] = useState<string | null>(
    initialCertificate ?? null,
  );

  const handleImageChange = (file: File) => {
    const fileUrl = URL.createObjectURL(file);
    setPreviewUrl(fileUrl);
  };

  const updatePreviewUrl = (previewUrl: string) => {
    setPreviewUrl(previewUrl);
  };

  return { previewUrl, handleImageChange, updatePreviewUrl };
};

export default usePreviewImage;
