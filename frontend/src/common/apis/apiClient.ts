import { API_ENDPOINTS } from '../constants/apiEndpoints';

import ApiError from './ApiError';
import { postReissue } from './postReissue';

interface ApiClientGetType {
  endpoint: string;
  searchParams?: Record<string, string>;
  withCredentials?: boolean;
}

interface ApiClientPostType<T> {
  endpoint: string;
  body?: Record<string, string | number> | FormData | T;
  withCredentials?: boolean;
}

interface ApiClientDeleteType {
  endpoint: string;
  withCredentials?: boolean;
}

interface ApiClientPatchType<T> {
  endpoint: string;
  body: Record<string, string | number> | T;
  withCredentials?: boolean;
}

interface ApiClientPutType<T> {
  endpoint: string;
  headers?: Record<string, string>;
  body: Record<string, string | number> | FormData | File | T;
  withCredentials?: boolean;
  useBaseUrl?: boolean;
}

type RequestCredentials = 'omit' | 'same-origin' | 'include';

let refreshPromise: Promise<void> | null = null;

const fetchRefresh = async () => {
  try {
    await postReissue();
  } catch (error) {
    console.error('토큰 갱신 실패', error);
    if (error instanceof ApiError) {
      throw new ApiError('토큰 갱신 실패', error.status);
    }
  }
};

const ensureRefreshed = async () => {
  if (!refreshPromise) {
    refreshPromise = fetchRefresh().finally(() => {
      refreshPromise = null;
    });
  }

  return refreshPromise;
};

class ApiClient {
  #baseUrl: string;

  constructor() {
    if (
      typeof process.env.API_BASE_URL === 'undefined' ||
      !process.env.API_BASE_URL
    ) {
      throw new Error(
        '환경 변수 BASE_URL이 설정되지 않았습니다. .env 파일을 확인해주세요.',
      );
    }
    this.#baseUrl = process.env.API_BASE_URL;
  }

  async get<T>({
    endpoint,
    searchParams,
    withCredentials,
  }: ApiClientGetType): Promise<T> {
    const url = new URL(`${this.#baseUrl}${endpoint}`);
    url.search = new URLSearchParams(searchParams).toString();

    const options = {
      method: 'GET',
      headers: {
        accept: 'application/json',
        'Content-Type': 'application/json',
      },
      credentials: withCredentials
        ? 'include'
        : ('same-origin' as RequestCredentials),
    };

    const sendRequest = async () => {
      const response = await fetch(url, options);
      if (!response.ok) {
        const data = await response.json();
        throw new ApiError(data.message, response.status);
      }

      return response.json();
    };

    try {
      if (refreshPromise) {
        await refreshPromise;
      }

      return await sendRequest();
    } catch (error) {
      if (
        error instanceof ApiError &&
        (error.status === 401 || error.status === 403)
      ) {
        try {
          await ensureRefreshed();
        } catch (error) {
          console.error(error);
          throw error;
        }

        try {
          return await sendRequest();
        } catch (error) {
          console.error('재요청 실패', error);
          if (error instanceof ApiError) {
            throw new ApiError('재요청 실패', error.status);
          }
        }
      }

      throw error;
    }
  }

  async post<T>({ endpoint, body, withCredentials }: ApiClientPostType<T>) {
    const url = new URL(`${this.#baseUrl}${endpoint}`);
    const isFormData = body instanceof FormData;

    const options = {
      method: 'POST',
      headers: isFormData ? undefined : { 'Content-Type': 'application/json' },
      body: isFormData ? body : JSON.stringify(body),
      credentials: withCredentials
        ? 'include'
        : ('same-origin' as RequestCredentials),
    };

    const sendRequest = async () => {
      const response = await fetch(url, options);
      if (!response.ok) {
        const data = await response.json();
        throw new ApiError(data.message, response.status);
      }

      return response;
    };

    return this.requestWithRefresh(sendRequest, endpoint);
  }

  async delete({ endpoint, withCredentials }: ApiClientDeleteType) {
    const url = new URL(`${this.#baseUrl}${endpoint}`);

    const options = {
      method: 'DELETE',
      headers: {
        accept: 'application/json',
      },
      credentials: withCredentials
        ? 'include'
        : ('same-origin' as RequestCredentials),
    };

    const sendRequest = async () => {
      const response = await fetch(url, options);
      if (!response.ok) {
        const data = await response.json();
        throw new ApiError(data.message, response.status);
      }
    };

    return this.requestWithRefresh(sendRequest);
  }

  async patch<T>({ endpoint, body, withCredentials }: ApiClientPatchType<T>) {
    const url = new URL(`${this.#baseUrl}${endpoint}`);

    const options = {
      method: 'PATCH',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(body),
      credentials: withCredentials
        ? 'include'
        : ('same-origin' as RequestCredentials),
    };

    const sendRequest = async () => {
      const response = await fetch(url, options);
      if (!response.ok) {
        const data = await response.json();
        throw new ApiError(data.message, response.status);
      }

      return response;
    };

    return this.requestWithRefresh(sendRequest);
  }

  async put<T>({
    endpoint,
    headers = { 'Content-Type': 'application/json' },
    body,
    withCredentials,
    useBaseUrl = true,
  }: ApiClientPutType<T>) {
    const url = new URL(`${useBaseUrl ? this.#baseUrl : ''}${endpoint}`);
    const isFormData = body instanceof FormData;
    const isFile = body instanceof File;

    const options = {
      method: 'PUT',
      headers: isFormData ? undefined : headers,
      body: isFormData || isFile ? body : JSON.stringify(body),
      credentials: withCredentials
        ? 'include'
        : ('same-origin' as RequestCredentials),
    };

    const sendRequest = async () => {
      const response = await fetch(url, options);
      if (!response.ok) {
        const data = await response.json();
        throw new ApiError(data.message, response.status);
      }

      return response;
    };

    return this.requestWithRefresh(sendRequest);
  }

  private async requestWithRefresh<T>(
    sendRequest: () => Promise<T>,
    endpoint?: string,
  ): Promise<T> {
    try {
      return await sendRequest();
    } catch (error) {
      if (
        error instanceof ApiError &&
        (error.status === 401 || error.status === 403) &&
        endpoint !== API_ENDPOINTS.REISSUE
      ) {
        try {
          await postReissue();
        } catch (error) {
          console.error('토큰 갱신 실패', error);
          if (error instanceof ApiError) {
            throw new ApiError('토큰 갱신 실패', error.status);
          }
        }

        try {
          return await sendRequest();
        } catch (error) {
          console.error('재요청 실패', error);
          if (error instanceof ApiError) {
            throw new ApiError('재요청 실패', error.status);
          }
        }
      }

      throw error;
    }
  }
}

export const apiClient = new ApiClient();
