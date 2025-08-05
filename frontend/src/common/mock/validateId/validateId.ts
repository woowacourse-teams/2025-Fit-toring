import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

interface AuthCodeBody {
  loginId: string;
}

const BASE_URL = process.env.BASE_URL;
const AUTH_CODE_URL = `${BASE_URL}${API_ENDPOINTS.VALIDATE_ID}`;

const DUMMY_DUPLICATE_USER_ID = 'test1234';

const postValidateId = http.post(AUTH_CODE_URL, async ({ request }) => {
  const body = await request.json();

  if (!body) {
    return new HttpResponse(null, { status: 400 });
  }

  const { loginId } = body as AuthCodeBody;

  if (loginId === DUMMY_DUPLICATE_USER_ID) {
    return HttpResponse.json(
      { message: '중복된 아이디입니다.' },
      { status: 400 },
    );
  }

  return HttpResponse.json(
    { message: '사용 가능한 아이디입니다.' },
    { status: 200 },
  );
});

export const validateIdHandler = [postValidateId];
