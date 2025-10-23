// 자격증 API 서비스
import { BASE_URL } from '../constants/config';

// 서버 응답 타입 정의
export interface CertificateListResponse {
    id: number;
    mentorName: string;
    certificateName: string;
    certificateType: string;
    certificateStatus: string;
    createdAt: string;
}

export interface CertificateDetailResponse {
    mentorName: string;
    certificateType: string;
    certificateStatus: string;
    imageUrl: string;
    createdAt: string;
}

// 클라이언트 사용 타입
export interface CertificateData {
    id: number;
    mentorName: string;
    mentorId: string;
    certificationName: string;
    certType: 'LICENSE' | 'EDUCATION' | 'AWARD' | 'ETC';
    issueDate: string;
    expiryDate: string | null;
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    documentUrl: string;
    imageUrl: string;
    submittedAt: string;
    rejectionReason?: string;
}

// API 기본 설정
const API_BASE_URL = BASE_URL;

// 헤더 생성
const getApiHeaders = (additionalHeaders?: Record<string, string>): HeadersInit => ({
    'Content-Type': 'application/json',
    ...additionalHeaders,
});

// 토큰 재발급
const reissueToken = async (): Promise<boolean> => {
    try {
        const response = await fetch(`${API_BASE_URL}/reissue`, {
            method: 'POST',
            headers: getApiHeaders(),
            credentials: 'include',
        });
        return response.ok;
    } catch (error) {
        console.error('토큰 재발급 실패:', error);
        return false;
    }
};

// 401 대응 fetch
const fetchWithTokenRefresh = async (url: string, options: RequestInit): Promise<Response> => {
    let response = await fetch(url, {
        ...options,
        credentials: 'include',
    });

    if (response.status === 401) {
        const reissueSuccess = await reissueToken();
        if (reissueSuccess) {
            response = await fetch(url, {
                ...options,
                credentials: 'include',
            });
        } else {
            alert("eeee :(href로 login 페이지로 이동)");
            window.location.href = '/web-admin/login';
            throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
        }
    }

    return response;
};

// 타입 매핑
const mapCertificateType = (type: string): 'LICENSE' | 'EDUCATION' | 'AWARD' | 'ETC' => {
    switch (type) {
        case 'LICENSE':
            return 'LICENSE';
        case 'EDUCATION':
            return 'EDUCATION';
        case 'AWARD':
            return 'AWARD';
        case 'ETC':
            return 'ETC';
        default:
            return 'ETC';
    }
};

const mapCertificateStatus = (status: string): 'PENDING' | 'APPROVED' | 'REJECTED' => {
    switch (status) {
        case 'PENDING':
            return 'PENDING';
        case 'APPROVED':
            return 'APPROVED';
        case 'REJECTED':
            return 'REJECTED';
        default:
            return 'PENDING';
    }
};

// 날짜 포맷
const formatDate = (dateString: string): string => {
    if(!dateString) return '';
    return dateString.split('T')[0];
};

// 응답 변환
const transformListResponse = (apiData: CertificateListResponse[]): CertificateData[] => {
    return apiData.map((item) => ({
        id: item.id,
        mentorName: item.mentorName,
        mentorId: `M${String(item.id).padStart(3, '0')}`,
        certificationName: item.certificateName,
        certType: mapCertificateType(item.certificateType),
        issueDate: formatDate(item.createdAt),
        expiryDate: null,
        status: mapCertificateStatus(item.certificateStatus),
        documentUrl: '#',
        imageUrl: '',
        submittedAt: formatDate(item.createdAt),
        ...(item.certificateStatus === 'REJECTED' && {rejectionReason: '관리자에 의해 반려됨'})
    }));
};

const transformDetailResponse = (apiData: CertificateDetailResponse, id: number): CertificateData => {
    return {
        id,
        mentorName: apiData.mentorName,
        mentorId: `M${String(id).padStart(3, '0')}`,
        certificationName: '',
        certType: mapCertificateType(apiData.certificateType),
        issueDate: formatDate(apiData.createdAt),
        expiryDate: null,
        status: mapCertificateStatus(apiData.certificateStatus),
        documentUrl: '#',
        imageUrl: apiData.imageUrl,
        submittedAt: formatDate(apiData.createdAt),
        ...(apiData.certificateStatus === 'REJECTED' && {rejectionReason: '관리자에 의해 반려됨'})
    };
};

export interface Paginated<T> {
    content: T[];
    page: number;
    size: number;
    total: number;
    totalPages: number;
}

export interface FetchCertificatesResult {
    certificates: CertificateData[];
    totalPages: number;
    totalElements: number;
}

// 목록 조회
export const fetchCertificates = async (
    statusFilter?: string,
    page: number = 0,
    size: number = 20
): Promise<FetchCertificatesResult> => {
    try {
        const queryParams = new URLSearchParams();
        if (statusFilter && statusFilter !== 'all') {
            const statusMap: { [key: string]: string } = {
                'PENDING': 'PENDING',
                'APPROVED': 'APPROVED',
                'REJECTED': 'REJECTED'
            };
            const apiStatus = statusMap[statusFilter];
            if (apiStatus) queryParams.append('type', apiStatus);
        }
        queryParams.append('page', String(page));
        queryParams.append('size', String(size));

        const url = `${API_BASE_URL}/admin/certificates?${queryParams.toString()}`;

        const response = await fetchWithTokenRefresh(url, {
            method: 'GET',
            headers: getApiHeaders(),
        });

        if (!response.ok) throw new Error(`자격증 목록 조회 실패: ${response.status} ${response.statusText}`);

        const apiData: Paginated<CertificateListResponse> = await response.json();

        return {
            certificates: transformListResponse(apiData.content),
            totalPages: apiData.totalPages,
            totalElements: apiData.total,
        };
    } catch (error) {
        console.error('자격증 목록 조회 실패:', error);
        throw error;
    }
};

// 상세 조회
export const fetchCertificateDetail = async (certificateId: number): Promise<CertificateData> => {
    try {
        const response = await fetchWithTokenRefresh(`${API_BASE_URL}/admin/certificates/${certificateId}`, {
            method: 'GET',
            headers: getApiHeaders(),
        });

        if (!response.ok) {
            throw new Error(`자격증 상세 조회 실패: ${response.status} ${response.statusText}`);
        }

        const apiData: CertificateDetailResponse = await response.json();
        return transformDetailResponse(apiData, certificateId);
    } catch (error) {
        console.error('자격증 상세 조회 실패:', error);
        throw error;
    }
};

// 승인
export const approveCertificate = async (id: number): Promise<void> => {
    try {
        const response = await fetchWithTokenRefresh(`${API_BASE_URL}/admin/certificates/${id}/approve`, {
            method: 'POST',
            headers: getApiHeaders(),
        });

        if (!response.ok) {
            throw new Error(`자격증 승인 실패: ${response.status} ${response.statusText}`);
        }
    } catch (error) {
        console.error('자격증 승인 실패:', error);
        throw error;
    }
};

// 반려
export const rejectCertificate = async (id: number): Promise<void> => {
    try {
        const response = await fetchWithTokenRefresh(`${API_BASE_URL}/admin/certificates/${id}/reject`, {
            method: 'POST',
            headers: getApiHeaders(),
        });

        if (!response.ok) {
            throw new Error(`자격증 반려 실패: ${response.status} ${response.statusText}`);
        }
    } catch (error) {
        console.error('자격증 반려 실패:', error);
        throw error;
    }
};

// 상태 변경 (레거시)
export const updateCertificateStatus = async (
    id: number,
    status: 'APPROVED' | 'REJECTED',
): Promise<void> => {
    if (status === 'REJECTED') {
        return rejectCertificate(id);
    } else if (status === 'APPROVED') {
        return approveCertificate(id);
    }
};
