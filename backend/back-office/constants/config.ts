/**
 * API 설정 상수
 * 
 * 서버 URL이 변경될 수 있으므로 상수로 분리하여 관리합니다.
 */
 export const BASE_URL = '';

/**
 * API 엔드포인트 상수
 */
export const API_ENDPOINTS = {
  // 인증 관련
  LOGIN: `${BASE_URL}/login`,
  REISSUE: `${BASE_URL}/reissue`,
  LOGOUT: `${BASE_URL}/logout`,
  
  // 사용자 정보
  AUTH_ME: `${BASE_URL}/members/me`,
  AUTH_STATUS: `${BASE_URL}/members/status`,
  MEMBER: `${BASE_URL}/admin/members`,
  MEMBER_MENTORING_CANDIDATES: `${BASE_URL}/members/mentor-candidates`,

  // 카테고리
  CATEGORIES: `${BASE_URL}/categories`,
  
  // 자격증명
  ADMIN_CERTIFICATES: `${BASE_URL}/admin/certificates`,
  CERTIFICATES: `${BASE_URL}/certificates`,
  
  // 멘토링
  ADMIN_MENTORING: `${BASE_URL}/admin/mentorings`,
  MENTORING: `${BASE_URL}/admin/mentorings`,
  MENTORING_DETAIL: `${BASE_URL}/mentorings/`,
  MENTORING_DELETE: `${BASE_URL}/admin/mentorings/`,
  MENTORING_CREATE: `${BASE_URL}/admin/mentorings`,

  // 멘토링 예약
  MENTORING_RESERVATION_PREFIX: `${BASE_URL}/admin/reservations/`,

  // 멘토링 리뷰
  MENTORING_REVIEW_PREFIX: `${BASE_URL}/admin/mentoring/`,
  MENTORING_REVIEW_POSTFIX: `reviews`,
} as const;

/**
 * API 요청 기본 설정
 */
export const DEFAULT_FETCH_OPTIONS: RequestInit = {
  credentials: 'include', // httpOnly 쿠키 자동 전송
  headers: {
    'Content-Type': 'application/json',
  },
};

/**
 * 환경 체크 헬퍼 함수
 */
const isDev = (): boolean => {
  try {
    // 브라우저 환경에서 hostname으로 판단
    if (typeof window !== 'undefined' && window.location) {
      return window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1';
    }
    // 기본값: 개발 환경으로 가정
    return true;
  } catch {
    return true;
  }
};

/**
 * 환경별 설정
 */
export const APP_CONFIG = {
  // 개발 환경 여부
  isDevelopment: isDev(),
  
  // API 요청 타임아웃 (밀리초)
  apiTimeout: 10000,
  
  // 토큰 갱신 재시도 횟수
  maxRetryAttempts: 1,
  
  // 디버그 로그 출력 여부
  enableDebugLogs: isDev(),
} as const;
