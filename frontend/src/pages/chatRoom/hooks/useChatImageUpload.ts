import { useCallback } from 'react';

import { putImageToS3 } from '../../../common/apis/putImageToS3';
import { postChatImagePresignedURL } from '../apis/postChatImagePresignedURL';

import type { ChatImageExtension } from '../apis/postChatImagePresignedURL';

const isChatImageExtension = (
  fileType: string,
): fileType is ChatImageExtension => {
  return ['png', 'jpg', 'jpeg', 'webp', 'avif'].includes(fileType);
};

const getExtension = (fileType: string): ChatImageExtension => {
  const extension = fileType.split('/')[1];
  if (!isChatImageExtension(extension)) {
    throw new Error('지원하지 않는 파일 형식입니다.');
  }

  return extension;
};

/**
 * 채팅 전용 이미지 업로드 hook.
 * 채팅방 전용 presigned URL(uploadId 포함)을 발급받아 S3에 업로드한다.
 * 메시지 전송에는 S3 URL/key 대신 서버가 발급한 uploadId를 사용한다.
 */
const useChatImageUpload = (chatRoomId: number) => {
  const upload = useCallback(
    async (file: File) => {
      try {
        const { uploadId, presignedUrl } = await postChatImagePresignedURL(
          chatRoomId,
          { extension: getExtension(file.type) },
        );

        await putImageToS3(presignedUrl, file);

        return { uploadId, uploadedUrl: presignedUrl.split('?')[0] };
      } catch (error) {
        if (error instanceof Error) {
          console.error('채팅 이미지 업로드 실패', error.message);
        }
        return { uploadId: '', uploadedUrl: '' };
      }
    },
    [chatRoomId],
  );

  return { upload };
};

export default useChatImageUpload;
