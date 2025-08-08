import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

const BASE_URL = process.env.API_BASE_URL;
const AHTH_CODE_URL = `${BASE_URL}${API_ENDPOINTS.AUTH_CODE}`;

const postAuthCode = http.post(AHTH_CODE_URL, async () => {
  return await HttpResponse.json(
    { message: '인증코드 발송 성공' },
    { status: 201 },
  );
});

export const authCodeHandler = [postAuthCode];
