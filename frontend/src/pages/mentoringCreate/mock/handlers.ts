import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';
import { getSpecialties } from '../../../common/mock/getSpecialties/handlers';
import { getUserInfoSummary } from '../../../common/mock/getUserInfoSummary/handler';

export const testStateStore = {
  shouldFailRequest: false,
  customRequestError: null as string | null,
  reset() {
    this.shouldFailRequest = false;
    this.customRequestError = null;
  },
};

const BASE_URL = process.env.API_BASE_URL;
const MENTORING_URL = `${BASE_URL}${API_ENDPOINTS.MENTORINGS}`;

const postMentoringCreate = http.post(MENTORING_URL, async ({ request }) => {
  const formData = await request.formData();

  const dataJson = formData.get('data');

  const parsedData = JSON.parse(dataJson as string);

  if (!parsedData) {
    return HttpResponse.json({ message: 'Bad Request' }, { status: 400 });
  }

  return HttpResponse.json({ message: true }, { status: 201 });
});

export const mentoringCreateHandler = [
  getUserInfoSummary,
  getSpecialties,
  postMentoringCreate,
];
