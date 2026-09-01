import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export type ChatImageExtension = 'png' | 'jpg' | 'jpeg' | 'webp' | 'avif';

export interface PostChatImagePresignedURLRequest {
  extension: ChatImageExtension;
}

export interface PostChatImagePresignedURLResponse {
  uploadId: string;
  presignedUrl: string;
  expiresAt: string;
}

const isPostChatImagePresignedURLResponse = (
  data: unknown,
): data is PostChatImagePresignedURLResponse => {
  return (
    typeof data === 'object' &&
    data !== null &&
    'uploadId' in data &&
    'presignedUrl' in data &&
    'expiresAt' in data
  );
};

export const postChatImagePresignedURL = async (
  chatRoomId: number,
  { extension }: PostChatImagePresignedURLRequest,
) => {
  const response = await apiClient.post({
    endpoint: `${API_ENDPOINTS.CHATROOMS}/${chatRoomId}/images/presigned`,
    body: { extension },
    withCredentials: true,
  });

  const data = await response.json();

  if (!isPostChatImagePresignedURLResponse(data)) {
    throw new Error(
      `채팅 이미지 presigned URL 응답 형식이 올바르지 않습니다: ${JSON.stringify(data)}`,
    );
  }

  return data;
};
