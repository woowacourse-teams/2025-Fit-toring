import { apiClient } from '../../../common/apis/apiClient';
import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

export const deleteCertificate = async (certificateId: string) => {
  return await apiClient.delete({
    endpoint: `${API_ENDPOINTS.CERTIFICATES}/${certificateId}`,
    withCredentials: true,
  });
};
