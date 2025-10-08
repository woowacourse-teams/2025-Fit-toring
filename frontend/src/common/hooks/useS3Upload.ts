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
    try {
      const data = await postPresignedURL({
        imageType,
        extension: getExtension(file.type),
      });

      const { presignedUrl } = data;

      await putImageToS3(presignedUrl, file);

      return { uploadedUrl: presignedUrl.split('?')[0] };
    } catch (error) {
      if (error instanceof Error) {
        console.error('S3 업로드 실패', error.message);
      }

      return { uploadedUrl: '' };
    }
  }, []);

  return { uploadFile };
};

export default useS3Upload;
