import { API_ENDPOINTS } from "@/constants/config";
import {
  getApiHeaders,
  getDefaultFetchOptions,
  joinUrl,
  fetchWithTokenRefresh,
} from "@/services/apiUtils";

type AnyObj = Record<string, any>;

export interface MentoringReview {
    id: number;
    menteeId: number;
    menteeName: string;
    rating: number;
    content: string;
    createdAt: string;
}

export interface MentoringReviewListResponse {
    ratingAverage: string;
    ratingCount: number;
    reviewData: MentoringReview[];
}
  
/**
 * 멘토링 리뷰 목록 조회
 */
export const fetchMentoringReviews = async (
    mentoringId: number,
  ): Promise<MentoringReviewListResponse> => {
    if (!mentoringId || Number.isNaN(mentoringId)) {
      throw new Error("유효하지 않은 멘토링 ID입니다.");
    }
  
    const base = API_ENDPOINTS.MENTORING_REVIEW_PREFIX;
    const postfix = API_ENDPOINTS.MENTORING_REVIEW_POSTFIX;
  
    const url = joinUrl(base, mentoringId, postfix);
  
    const res = await fetchWithTokenRefresh(url, {
      method: "GET",
      ...getDefaultFetchOptions(),
      headers: getApiHeaders(),
    });
  
    if (!res.ok) {
      throw new Error(
        `멘토링 리뷰 목록 조회 실패: ${res.status} ${res.statusText}`,
      );
    }
  
    const json = (await res.json()) as AnyObj;
  
    const ratingAverage = String(json.ratingAverage ?? "0");
    const ratingCount = Number(json.ratingCount ?? 0);
    const reviewDataRaw: AnyObj[] = Array.isArray(json.reviewData)
      ? json.reviewData
      : [];
  
    const reviewData: MentoringReview[] = reviewDataRaw.map((r) => ({
      id: Number(r.id),
      menteeId: Number(r.menteeId ?? r.mentee_id ?? 0),
      menteeName: String(r.menteeName ?? r.mentee_name ?? ""),
      rating: Number(r.rating ?? 0),
      content: String(r.content ?? ""),
      createdAt: String(r.createdAt ?? ""),
    }));
  
    return { ratingAverage, ratingCount, reviewData };
  };
  
  /**
 * 리뷰 삭제 API
 */
export const deleteReview = async (reviewId: number): Promise<void> => {
    if (!reviewId || Number.isNaN(reviewId)) {
      throw new Error("유효하지 않은 리뷰 ID입니다.");
    }
  
    const url = joinUrl(API_ENDPOINTS.MENTORING_REVIEW_DELETE, reviewId);
  
    const res = await fetchWithTokenRefresh(url, {
      method: "DELETE",
      ...getDefaultFetchOptions(),
      headers: getApiHeaders(),
    });
  
    if (!res.ok) {
      const text = await res.text().catch(() => "");
      throw new Error(
        `리뷰 삭제 실패: ${res.status} ${res.statusText} ${text}`,
      );
    }
  };