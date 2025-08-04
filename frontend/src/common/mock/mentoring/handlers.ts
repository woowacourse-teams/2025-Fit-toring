import { http, HttpResponse } from 'msw';

import { MENTORINGS } from './data';

export const testStateStore = {
  shouldFailCart: false,
  customCartError: null as string | null,
  reset() {
    this.shouldFailCart = false;
    this.customCartError = null;
  },
};

const BASE_URL = process.env.BASE_URL;
const MENTORING_URL = `${BASE_URL}/mentorings`;
const getMentoringItems = http.get(MENTORING_URL, ({ request }) => {
  const url = new URL(request.url);
  const { searchParams } = url;

  const categoryTitle1 = searchParams.get('categoryTitle1');
  const categoryTitle2 = searchParams.get('categoryTitle2');
  const categoryTitle3 = searchParams.get('categoryTitle3');

  const categoryValues = [
    categoryTitle1,
    categoryTitle2,
    categoryTitle3,
  ].filter((category) => category !== null) as string[];

  if (categoryValues.length > 0) {
    const response = MENTORINGS.filter((mentor) =>
      categoryValues.every((category) => mentor.categories.includes(category)),
    );

    if (testStateStore.shouldFailCart) {
      return new HttpResponse(null, {
        status: 500,
        statusText: 'filtered mentorings fetch Failed',
      });
    }

    return HttpResponse.json(response);
  } else {
    const response = [...MENTORINGS];

    if (testStateStore.shouldFailCart) {
      return new HttpResponse(null, {
        status: 500,
        statusText: 'mentorings fetch Failed',
      });
    }

    return HttpResponse.json(response);
  }
});

export const mentoringHandler = [getMentoringItems];
