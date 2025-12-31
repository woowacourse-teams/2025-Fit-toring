import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';
import { SPECIALTIES } from '../../../common/mock/getSpecialties/data';
import { getSpecialties } from '../../../common/mock/getSpecialties/handlers';

import { MENTORINGS } from './data';

export const testStateStore = {
  shouldFailRequest: false,
  customRequestError: null as string | null,
  reset() {
    this.shouldFailRequest = false;
    this.customRequestError = null;
  },
};

const BASE_URL = process.env.API_BASE_URL;
const MENTORING_URL = `${BASE_URL}${API_ENDPOINTS.MENTORINGS_PAGE}`;
const getMentorList = http.get(MENTORING_URL, ({ request }) => {
  const url = new URL(request.url);
  const { searchParams } = url;

  const searchParamsCategoryIds = searchParams.get('categoryIds') ?? '';
  const categoryIds = searchParamsCategoryIds
    .split(',')
    .filter((val) => val !== '' || val !== null)
    .map(Number);

  const categoryValues = SPECIALTIES.filter(({ id }) =>
    categoryIds.includes(id),
  );

  if (categoryValues.length > 0) {
    const response = {
      hasNext: false,
      mentoringSummaryResponses: MENTORINGS.filter((mentor) =>
        categoryValues.every(({ title }) => mentor.categories.includes(title)),
      ),
      nextCursorCode: null,
    };

    if (testStateStore.shouldFailRequest) {
      return new HttpResponse(
        { message: 'filtered mentorings fetch Failed' },
        {
          status: 500,
        },
      );
    }

    return HttpResponse.json(response);
  } else {
    const response = {
      hasNext: false,
      mentoringSummaryResponses: [...MENTORINGS],
      nextCursorCode: null,
    };

    if (testStateStore.shouldFailRequest) {
      return new HttpResponse(
        { message: 'mentorings fetch Failed' },
        {
          status: 500,
        },
      );
    }

    return HttpResponse.json(response);
  }
});

export const homeHandler = [getSpecialties, getMentorList];
