import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

const BASE_URL = process.env.BASE_URL;
const SIGNUP_URL = `${BASE_URL}${API_ENDPOINTS.SIGNUP}`;

const postSignup = http.post(SIGNUP_URL, async () => {
  return await HttpResponse.json({ message: '회원가입 성공' }, { status: 201 });
});

export const signupHandler = [postSignup];
