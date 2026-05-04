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

export const fetchDummyStatus = async (fileSeq: number): Promise<DummyStatus> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, fileSeq);
    const res = await fetchWithTokenRefresh(url, { 
        method: "GET",
        ...getDefaultFetchOptions() 
    });
    
    if (!res.ok) throw new Error("상태 조회 실패");
    return await res.json();
};

export const insertDummyScenario = async (fileSeq: number): Promise<DummyInsertResponse> => {
    const url = joinUrl(API_ENDPOINTS.ADMIN_DUMMY_SQL_INSERT, fileSeq);
    const res = await fetchWithTokenRefresh(url, {
        method: "POST",
        ...getDefaultFetchOptions(),
        headers: getApiHeaders(),
    });
    if (!res.ok) throw new Error("더미 데이터 적재 실패");
    return await res.json();
};
