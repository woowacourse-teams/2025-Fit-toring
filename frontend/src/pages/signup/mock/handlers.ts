import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';
import { postAuthCode } from '../../../common/mock/authCode/handlers';
import { postAuthCodeVerify } from '../../../common/mock/authCodeVerify/handlers';

const postSignup = http.post(`*${API_ENDPOINTS.SIGNUP}`, async () => {
  return await HttpResponse.json({ message: '회원가입 성공' }, { status: 201 });
});

interface AuthCodeBody {
  loginId: string;
}

const DUMMY_DUPLICATE_USER_ID = 'test1234';

const postValidateId = http.post(
  `*${API_ENDPOINTS.VALIDATE_ID}`,
  async ({ request }) => {
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
  },
);

export const signupHandler = [
  postSignup,
  postValidateId,
  postAuthCode,
  postAuthCodeVerify,
];
