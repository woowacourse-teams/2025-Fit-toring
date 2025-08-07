// 자격증 API 서비스

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
    certType: 'LICENSE' | 'EDUCATION' | 'AWARD' | 'ECT';
    issueDate: string;
    expiryDate: string | null;
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    documentUrl: string;
    imageUrl: string;
    submittedAt: string;
    rejectionReason?: string;
}

// 쿠키 유틸
const getCookie = (name: string): string | null => {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i];
        while (c.charAt(0) === ' ') c = c.substring(1, c.length);
        if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length, c.length);
    }
    return null;
};

// API 기본 설정
const API_BASE_URL = '';

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
            window.location.href = '/login';
            throw new Error('인증이 만료되었습니다. 다시 로그인해주세요.');
        }
    }

    return response;
};

// 타입 매핑
const mapCertificateType = (type: string): 'LICENSE' | 'EDUCATION' | 'AWARD' | 'ECT' => {
    switch (type) {
        case '자격증':
            return 'LICENSE';
        case '학력':
            return 'EDUCATION';
        case '수상':
            return 'AWARD';
        case '기타':
            return 'ECT';
        default:
            return 'LICENSE';
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
    return dateString.split(' ')[0];
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
        ...(apiData.certificateStatus === 'REJECT' && {rejectionReason: '관리자에 의해 반려됨'})
    };
};

// 목록 조회
export const fetchCertificates = async (statusFilter?: string): Promise<CertificateData[]> => {
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

        const url = `${API_BASE_URL}/admin/certificates${queryParams.toString() ? '?' + queryParams.toString() : ''}`;
        const debugHeaders: Record<string, string> = {};
        if (statusFilter && statusFilter !== 'all') {
            debugHeaders['X-Filter-Status'] = statusFilter;
        }

        const response = await fetchWithTokenRefresh(url, {
            method: 'GET',
            headers: getApiHeaders(debugHeaders),
        });

        if (!response.ok) throw new Error(`자격증 목록 조회 실패: ${response.status} ${response.statusText}`);

        const apiData: CertificateListResponse[] = await response.json();

        apiData.sort((a, b) => {
            const statusOrder = {'PENDING': 0, 'REJECT': 1, 'VERIFIED': 2};
            return statusOrder[a.certificateStatus] - statusOrder[b.certificateStatus];
        });

        return transformListResponse(apiData);
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
    rejectionReason?: string
): Promise<void> => {
    if (status === 'REJECTED') {
        return rejectCertificate(id);
    } else if (status === 'APPROVED') {
        return approveCertificate(id);
    }
};
