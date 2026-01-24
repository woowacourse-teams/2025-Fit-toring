import { API_ENDPOINTS } from "@/constants/config";
import { getApiHeaders, getDefaultFetchOptions, fetchWithTokenRefresh } from "@/services/apiUtils";

export interface DeviceItem {
  id: number;
  memberName: string;
  memberId: number;
  pushToken: string;
}

export interface PageResult<T> {
  content: T[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
}

export const fetchDevices = async (page: number, size: number) => {
  const url = `${API_ENDPOINTS.ADMIN_DEVICES}?page=${page}&size=${size}`;

  const res = await fetchWithTokenRefresh(url, {
    method: "GET",
    ...getDefaultFetchOptions(),
    headers: getApiHeaders(),
  });

  if (!res.ok) {
    throw new Error(`기기 목록 조회 실패: ${res.status} ${res.statusText}`);
  }

  const data = await res.json();

  return {
    content: data.content || [],
    page: data.page,
    size: data.size,
    total: data.total,
    totalPages: data.totalPages,
  } as PageResult<DeviceItem>;
};

export const deleteDevice = async (id: number) => {
  const url = `${API_ENDPOINTS.ADMIN_DEVICES}/${id}`;

  const res = await fetchWithTokenRefresh(url, {
    method: "DELETE",
    ...getDefaultFetchOptions(),
    headers: getApiHeaders(),
  });

  if (!res.ok) {
    throw new Error(`기기 삭제 실패: ${res.status} ${res.statusText}`);
  }
};
