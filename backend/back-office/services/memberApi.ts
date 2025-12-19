import { API_ENDPOINTS, BASE_URL } from "@/constants/config";
import { getApiHeaders, getDefaultFetchOptions, fetchWithTokenRefresh, joinUrl } from "@/services/apiUtils";


export interface MemberItem {
    name: string;
    loginId: string;
    gender: string;
    phoneNumber: string;
    role: "MENTOR" | "MENTEE" | "ADMIN";
  }
  
  export const fetchMembers = async (page: number, size: number) => {
    // 서버 page는 1-based라고 가정
    const url = `${API_ENDPOINTS.MEMBER}?page=${page}&size=${size}`;
  
    const res = await fetchWithTokenRefresh(url, {
      method: "GET",
      ...getDefaultFetchOptions(),
      headers: getApiHeaders(),
    });
  
    if (!res.ok) {
      throw new Error(`회원 목록 조회 실패: ${res.status} ${res.statusText}`);
    }
  
    const data = await res.json();
  
    return {
      content: data.content || [],
      page: data.page,
      size: data.size,
      total: data.total,
      totalPages: data.totalPages,
    };
  };
