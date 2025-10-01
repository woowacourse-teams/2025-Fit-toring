import { API_ENDPOINTS } from '../constants/apiEndpoints';

import { apiClient } from './apiClient';

export interface PostPresignedURLRequest {
  imageType: 'MENTORING_PROFILE' | 'CERTIFICATE';
  extension: 'png' | 'jpg' | 'jpeg' | 'webp' | 'avif';
}

export interface PostPresignedURLResponse {
  presignedUrl: string;
  expiresAt: string;
}

export const postPresignedURL = async (request: PostPresignedURLRequest) => {
  return await apiClient.post({
    endpoint: API_ENDPOINTS.REQUEST_PRESIGNED_URL,
    body: { ...request },
    withCredentials: true,
  });
};
