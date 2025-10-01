import { apiClient } from './apiClient';

export const putImageToS3 = async (preSignedUrl: string, file: File) => {
  return await apiClient.put({
    endpoint: preSignedUrl,
    body: file,
    headers: { 'Content-Type': file.type },
  });
};
