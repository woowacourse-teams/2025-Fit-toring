import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';
import { postAuthCode } from '../../../common/mock/authCode/handlers';

const BASE_URL = process.env.API_BASE_URL;
const SIGNUP_URL = `${BASE_URL}${API_ENDPOINTS.SIGNUP}`;

const postSignup = http.post(SIGNUP_URL, async () => {
  return await HttpResponse.json({ message: '회원가입 성공' }, { status: 201 });
});

interface AuthCodeBody {
  loginId: string;
}

const AUTH_CODE_URL = `${BASE_URL}${API_ENDPOINTS.VALIDATE_ID}`;

const DUMMY_DUPLICATE_USER_ID = 'test1234';

const postValidateId = http.post(AUTH_CODE_URL, async ({ request }) => {
  const body = await request.json();

  if (!body) {
    return await new HttpResponse(null, { status: 400 });
  }

  const { loginId } = body as AuthCodeBody;

  if (loginId === DUMMY_DUPLICATE_USER_ID) {
    return await HttpResponse.json(
      { message: '중복된 아이디입니다.' },
      { status: 400 },
    );
  }

  return await HttpResponse.json(
    { message: '사용 가능한 아이디입니다.' },
    { status: 200 },
  );
});

export const signupHandler = [postSignup, postValidateId, postAuthCode];
