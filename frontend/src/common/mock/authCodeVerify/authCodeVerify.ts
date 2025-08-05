import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

interface AuthCodeVerifyBody {
  code: string;
}

const BASE_URL = process.env.BASE_URL;

const DUMMY_AUTH_CODE = '123456';

const postAuthCodeVerify = http.post(
  `${BASE_URL}${API_ENDPOINTS.AUTH_CODE_VERIFY}`,
  async ({ request }) => {
    const body = await request.json();

    if (!body) {
      return new HttpResponse(null, { status: 400 });
    }

    const { code } = body as AuthCodeVerifyBody;

    if (code === DUMMY_AUTH_CODE) {
      return HttpResponse.json({ message: '인증 완료' }, { status: 200 });
    }
  },
);

export const authCodeVerifyHandler = [postAuthCodeVerify];
