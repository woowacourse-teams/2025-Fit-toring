import { API_ENDPOINTS, BASE_URL } from "@/constants/config";
import {
  getApiHeaders,
  getDefaultFetchOptions,
  fetchWithTokenRefresh,
  joinUrl,
} from "@/services/apiUtils";

export interface ReservationItemResponse {
  reservationId: number;
  menteeName: string;
  createdAt: string;
  status: "PENDING" | "APPROVED" | "REJECTED" | "COMPLETE";
  content: string;
}

export interface ReservationPageResponse {
  content: ReservationItemResponse[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
}

export interface Reservation {
  id: number;
  menteeName: string;
  createdAt: string;
  status: "PENDING" | "APPROVED" | "REJECTED" | "COMPLETE";
  content: string;
}

const toReservation = (src: ReservationItemResponse): Reservation => ({
  id: src.reservationId,
  menteeName: src.menteeName,
  createdAt: src.createdAt,
  status: src.status,
  content: src.content,
});

/**
 * 예약 목록 조회 (서버 페이지네이션 적용)
 */
export const fetchReservations = async (
  mentoringId: number,
  page: number,
  size: number
): Promise<{
  items: Reservation[];
  page: number;
  size: number;
  total: number;
  totalPages: number;
}> => {
  try {
    const base = (API_ENDPOINTS as any).ADMIN_MENTORING ?? "/admin/mentorings";

    const url = `${joinUrl(base, mentoringId, "reservations")}?page=${page}&size=${size}`;

    const res = await fetchWithTokenRefresh(url, {
      method: "GET",
      ...getDefaultFetchOptions(),
      headers: getApiHeaders(),
    });

    if (!res.ok) {
      throw new Error(
        `예약 목록 조회 실패: ${res.status} ${res.statusText}`
      );
    }

    const json = (await res.json()) as ReservationPageResponse;

    return {
      items: json.content.map(toReservation),
      page: json.page,
      size: json.size,
      total: json.total,
      totalPages: json.totalPages,
    };
  } catch (e) {
    console.error("예약 목록 조회 실패:", e);
    throw e;
  }
};

/**
 * 예약 상태 변경
 */
export type ReservationStatus = Reservation["status"];

export const fetchUpdateStatusReservation = async (
  reservationId: number,
  status: ReservationStatus
): Promise<void> => {
  const base =
    (API_ENDPOINTS as any).MENTORING_RESERVATION_PREFIX ??
    [BASE_URL.replace(/\/$/, ""), "admin", "reservations"].join("/");

  const url = joinUrl(base, reservationId, "status");

  const res = await fetchWithTokenRefresh(url, {
    method: "PATCH",
    ...getDefaultFetchOptions(),
    headers: getApiHeaders(),
    body: JSON.stringify({ status }),
  });

  if (!(res.ok || res.status === 200)) {
    console.warn(
      `예약 상태 수정 실패: ${res.status} ${res.statusText}`
    );
  }
};

/**
 * 예약 삭제
 */
export const fetchDeleteReservation = async (
  reservationId: number
): Promise<void> => {
  const base =
    (API_ENDPOINTS as any).MENTORING_RESERVATION_PREFIX ??
    [BASE_URL.replace(/\/$/, ""), "admin", "reservations"].join("/");

  const url = joinUrl(base, reservationId);

  const res = await fetchWithTokenRefresh(url, {
    method: "DELETE",
    ...getDefaultFetchOptions(),
    headers: getApiHeaders(),
  });

  if (res.status !== 204) {
    console.warn(`예약 삭제 실패: ${res.status} ${res.statusText}`);
  }
};
