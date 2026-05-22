import { API_ENDPOINTS } from "@/constants/config";
import { getApiHeaders, getDefaultFetchOptions, fetchWithTokenRefresh, joinUrl } from "@/services/apiUtils";

export interface DummyStatus {
    fileSeq: number;
    scenarioFile: string;
    inserted: boolean;
    appliedStartAt: string | null;
    originalDuration: string;
}

export interface DummyInsertResponse {
    fileSeq: number;
    scenarioFile: string;
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
    fileSeq: number;
    scenarioFile: string;
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

export const fetchDummyStatus = async (fileSeq: number): Promise<DummyStatus> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, fileSeq);
    const res = await fetchWithTokenRefresh(url, { 
        method: "GET",
        ...getDefaultFetchOptions() 
    });
    
    if (!res.ok) throw new Error("상태 조회 실패");
    return await res.json();
};

export const fetchDummyPreview = async (fileSeq: number): Promise<DummyScenarioPreview> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, fileSeq, "preview");
    const res = await fetchWithTokenRefresh(url, {
        method: "GET",
        ...getDefaultFetchOptions()
    });

    if (!res.ok) throw new Error("시나리오 미리보기 조회 실패");
    return await res.json();
};

export const insertDummyScenario = async (
    fileSeq: number,
    startAt: string,
    duration?: string
): Promise<DummyInsertResponse> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, fileSeq);
    const res = await fetchWithTokenRefresh(url, {
        method: "POST",
        ...getDefaultFetchOptions(),
        headers: getApiHeaders(),
        body: JSON.stringify({ startAt, duration }),
    });
    if (!res.ok) throw new Error("더미 데이터 적재 실패");
    return await res.json();
};
