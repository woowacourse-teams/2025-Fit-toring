import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

interface LoginBody {
  loginId: string;
  password: string;
}

const BASE_URL = process.env.BASE_URL;
const LOGIN_URL = `${BASE_URL}${API_ENDPOINTS.LOGIN}`;

const DUMMY_USER_ID = 'test1234';
const DUMMY_PASSWORD = 'password1234';

const postLogin = http.post(LOGIN_URL, async ({ request }) => {
  const body = await request.json();

  if (!body) {
    return await new HttpResponse(null, { status: 400 });
  }

  const { loginId, password } = body as LoginBody;

  if (loginId === DUMMY_USER_ID && password === DUMMY_PASSWORD) {
    return await HttpResponse.json({ message: '로그인 성공' }, { status: 200 });
  }

  return await new HttpResponse(null, { status: 400 });
});

export const loginHandler = [postLogin];
