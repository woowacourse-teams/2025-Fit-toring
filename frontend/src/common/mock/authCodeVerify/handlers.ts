import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

interface AuthCodeVerifyBody {
  code: string;
}

const DUMMY_AUTH_CODE = '123456';

const BASE_URL = process.env.API_BASE_URL;
const AUTH_CODE_VERIFY_URL = `${BASE_URL}${API_ENDPOINTS.AUTH_CODE_VERIFY}`;

export const postAuthCodeVerify = http.post(
  `${AUTH_CODE_VERIFY_URL}`,
  async ({ request }) => {
    const body = await request.json();

    if (!body) {
      return await new HttpResponse(null, { status: 400 });
    }

    const { code } = body as AuthCodeVerifyBody;

    if (code === DUMMY_AUTH_CODE) {
      return await HttpResponse.json({ message: '인증 완료' }, { status: 200 });
    }
  },
);
