import { API_ENDPOINTS } from "@/constants/config";
import { getApiHeaders, getDefaultFetchOptions, fetchWithTokenRefresh, joinUrl } from "@/services/apiUtils";

export interface DummyStatus {
    scenarioId: number;
    originalFilename: string;
    status: 'UPLOADED' | 'INSERTED';
    uploadedAt: string;
    insertedAt: string | null;
    appliedStartAt: string | null;
    originalDuration: string;
    appliedDuration: string | null;
    postCount: number;
    commentCount: number;
}

export interface DummyInsertResponse {
    scenarioId: number;
    originalFilename: string;
    insertedScenarioCount: number;
    insertedPostPendingCount: number;
    insertedCommentPendingCount: number;
    status: 'INSERTED';
    appliedStartAt: string;
    appliedDuration: string;
}

export interface DummyCommentPreview {
    nickname: string;
    scheduledAt: string;
    content: string;
    replies: DummyCommentPreview[];
}

export interface DummyPostPreview {
    nickname: string;
    scheduledAt: string;
    title: string;
    content: string;
    comments: DummyCommentPreview[];
}

export interface DummyScenarioPreview {
    scenarioId: number;
    originalFilename: string;
    originalDuration: string;
    posts: DummyPostPreview[];
}

export const fetchDummyScenarios = async (): Promise<DummyStatus[]> => {
    const res = await fetchWithTokenRefresh(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, {
        method: "GET",
        ...getDefaultFetchOptions()
    });

    if (!res.ok) throw new Error("시나리오 목록 조회 실패");
    return await res.json();
};

export const fetchDummyStatus = async (scenarioId: number): Promise<DummyStatus> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, scenarioId);
    const res = await fetchWithTokenRefresh(url, { 
        method: "GET",
        ...getDefaultFetchOptions() 
    });
    
    if (!res.ok) throw new Error("상태 조회 실패");
    return await res.json();
};

export const fetchDummyPreview = async (scenarioId: number): Promise<DummyScenarioPreview> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, scenarioId, "preview");
    const res = await fetchWithTokenRefresh(url, {
        method: "GET",
        ...getDefaultFetchOptions()
    });

    if (!res.ok) throw new Error("시나리오 미리보기 조회 실패");
    return await res.json();
};

export const insertDummyScenario = async (
    scenarioId: number,
    startAt: string,
    duration?: string
): Promise<DummyInsertResponse> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, scenarioId);
    const res = await fetchWithTokenRefresh(url, {
        method: "POST",
        ...getDefaultFetchOptions(),
        headers: getApiHeaders(),
        body: JSON.stringify({ startAt, duration }),
    });
    if (!res.ok) throw new Error("더미 데이터 적재 실패");
    return await res.json();
};

export const deleteDummyScenario = async (scenarioId: number): Promise<void> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, scenarioId);
    const res = await fetchWithTokenRefresh(url, {
        method: "DELETE",
        ...getDefaultFetchOptions(),
    });

    if (!res.ok) {
        let errorMessage = "시나리오 삭제 실패";
        try {
            const body = await res.json();
            if (body && typeof body.message === "string" && body.message.trim()) {
                errorMessage = body.message;
            }
        } catch {
            // 본문이 JSON이 아니면 기본 메시지 사용
        }
        throw new Error(errorMessage);
    }
};

export const uploadDummyScenario = async (file: File): Promise<DummyStatus> => {
    const formData = new FormData();
    formData.append("file", file);

    // multipart는 fetch가 boundary 포함 Content-Type을 직접 설정해야 하므로
    // getDefaultFetchOptions()의 application/json 헤더를 적용하지 않음.
    const res = await fetchWithTokenRefresh(API_ENDPOINTS.ADMIN_DUMMY_SQL_UPLOAD, {
        method: "POST",
        credentials: "include",
        body: formData,
    });

    if (!res.ok) {
        let errorMessage = "YAML 업로드 실패";
        try {
            const body = await res.json();
            if (body && typeof body.message === "string" && body.message.trim()) {
                errorMessage = body.message;
            }
        } catch {
            // 본문이 JSON이 아니면 기본 메시지 사용
        }
        throw new Error(errorMessage);
    }
    return await res.json();
};
