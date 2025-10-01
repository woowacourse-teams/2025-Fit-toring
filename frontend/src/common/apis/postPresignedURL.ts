import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

export interface PostPresignedURLRequest {
  imageType: 'MENTORING_PROFILE' | 'CERTIFICATE';
  extension: 'png' | 'jpg' | 'jpeg' | 'webp' | 'avif';
}

interface PostPresignedURLResponse {
  presignedUrl: string;
  expiresAt: string;
}

const isPostPresignedURLResponse = (
  data: unknown,
): data is PostPresignedURLResponse => {
  return (
    typeof data === 'object' &&
    data !== null &&
    'presignedUrl' in data &&
    'expiresAt' in data
  );
};

export const postPresignedURL = async (request: PostPresignedURLRequest) => {
  const response = await apiClient.post({
    endpoint: API_ENDPOINTS.REQUEST_PRESIGNED_URL,
    body: { ...request },
    withCredentials: true,
  });

  const data = await response.json();

  if (!isPostPresignedURLResponse(data)) {
    throw new Error('presigned URL 응답 형식이 올바르지 않습니다.');
  }

  return data;
};
