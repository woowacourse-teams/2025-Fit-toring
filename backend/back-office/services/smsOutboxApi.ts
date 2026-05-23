import { API_ENDPOINTS } from "@/constants/config";
import { getApiHeaders, getDefaultFetchOptions, fetchWithTokenRefresh } from "@/services/apiUtils";

export type SmsOutboxStatus = "PENDING" | "PROCESSING" | "SENT" | "FAILED";
export type SmsOutboxEventType = "RESERVATION_CREATED" | "APPROVED" | "REJECTED";

export interface SmsOutboxItem {
  id: number;
  reservationId: number;
  eventType: SmsOutboxEventType;
  toPhone: string;
  status: SmsOutboxStatus;
  attempts: number;
  lastError: string | null;
  failedNotifiedAt: string | null;
  createdAt: string;
  updatedAt: string;
}

export interface SmsOutboxDetail extends SmsOutboxItem {
  message: string;
  subject: string;
  processingStartedAt: string | null;
}

export interface PageResult<T> {
  content: T[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
}

export const fetchSmsOutboxList = async (
  status: SmsOutboxStatus,
  page: number,
  size: number
): Promise<PageResult<SmsOutboxItem>> => {
  const url = `${API_ENDPOINTS.ADMIN_SMS_OUTBOX}?status=${status}&page=${page}&size=${size}`;

  const res = await fetchWithTokenRefresh(url, {
    method: "GET",
    ...getDefaultFetchOptions(),
    headers: getApiHeaders(),
  });

  if (!res.ok) {
    throw new Error(`SMS Outbox 목록 조회 실패: ${res.status} ${res.statusText}`);
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

export const fetchSmsOutboxDetail = async (id: number): Promise<SmsOutboxDetail> => {
  const url = `${API_ENDPOINTS.ADMIN_SMS_OUTBOX}/${id}`;

  const res = await fetchWithTokenRefresh(url, {
    method: "GET",
    ...getDefaultFetchOptions(),
    headers: getApiHeaders(),
  });

  if (!res.ok) {
    throw new Error(`SMS Outbox 상세 조회 실패: ${res.status} ${res.statusText}`);
  }

  return (await res.json()) as SmsOutboxDetail;
};

export const retrySmsOutbox = async (id: number): Promise<void> => {
  const url = `${API_ENDPOINTS.ADMIN_SMS_OUTBOX}/${id}/retry`;

  const res = await fetchWithTokenRefresh(url, {
    method: "POST",
    ...getDefaultFetchOptions(),
    headers: getApiHeaders(),
  });

  if (!res.ok) {
    const message = res.status === 409
        ? "FAILED 상태의 row만 재시도할 수 있습니다."
        : `SMS Outbox 재시도 실패: ${res.status} ${res.statusText}`;
    throw new Error(message);
  }
};
