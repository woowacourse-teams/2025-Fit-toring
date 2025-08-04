import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

interface AuthCodeVerifyBody {
  authcode: string;
}

const BASE_URL = process.env.BASE_URL;

const DUMMY_AUTH_CODE = '123456';

const postAuthCodeVerify = http.post(
  `${BASE_URL}${API_ENDPOINTS.AUTH_CODE_VERIFY}`,
  async ({ request }) => {
    const body = await request.json();

    if (!body) {
      return;
    }

    const { authcode } = body as AuthCodeVerifyBody;

    if (authcode === DUMMY_AUTH_CODE) {
      return HttpResponse.json({ message: '인증 완료' }, { status: 200 });
    }
  },
);

export const postAuthCodeVerifyHandler = [postAuthCodeVerify];
