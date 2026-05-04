import { API_ENDPOINTS } from "@/constants/config";
import { getApiHeaders, getDefaultFetchOptions, fetchWithTokenRefresh, joinUrl } from "@/services/apiUtils";

export interface DummyStatus {
    fileSeq: number;
    scenarioFile: string;
    inserted: boolean;
}

export interface DummyInsertResponse {
    fileSeq: number;
    scenarioFile: string;
    insertedScenarioCount: number;
    insertedPostPendingCount: number;
    insertedCommentPendingCount: number;
    status: 'INSERTED';
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

export const insertDummyScenario = async (fileSeq: number, startAt: string): Promise<DummyInsertResponse> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, fileSeq);
    const res = await fetchWithTokenRefresh(url, {
        method: "POST",
        ...getDefaultFetchOptions(),
        headers: getApiHeaders(),
        body: JSON.stringify({ startAt }),
    });
    if (!res.ok) throw new Error("더미 데이터 적재 실패");
    return await res.json();
};
