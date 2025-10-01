import { useCallback } from 'react';

import { postPresignedURL } from '../apis/postPresignedURL';
import { putImageToS3 } from '../apis/putImageToS3';

import type { PostPresignedURLRequest } from '../apis/postPresignedURL';

type ImageType = PostPresignedURLRequest['imageType'];
type Extension = PostPresignedURLRequest['extension'];

const isExtension = (fileType: string): fileType is Extension => {
  return ['png', 'jpg', 'jpeg', 'webp', 'avif'].includes(fileType);
};

const getExtension = (fileType: string) => {
  const extension = fileType.split('/')[1];
  if (!isExtension(extension)) {
    throw new Error('지원하지 않는 파일 형식입니다.');
  }

  return extension;
};

const useS3Upload = () => {
  const uploadFile = useCallback(async (file: File, imageType: ImageType) => {
    const data = await postPresignedURL({
      imageType,
      extension: getExtension(file.type),
    });

    const { presignedUrl } = data;

    const response = await putImageToS3(presignedUrl, file);

    if (response.ok) {
      return { uploadedUrl: presignedUrl.split('?')[0] };
    }
    return { uploadedUrl: '' };
  }, []);

  return { uploadFile };
};

export default useS3Upload;
