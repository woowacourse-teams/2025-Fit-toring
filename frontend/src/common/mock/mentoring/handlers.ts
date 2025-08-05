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
const getMentoringItems = http.get(MENTORING_URL, () => {
  const response = [...MENTORINGS];

  if (testStateStore.shouldFailCart) {
    return new HttpResponse(null, {
      status: 500,
      statusText: 'mentorings fetch Failed',
    });
  }

  return HttpResponse.json(response);
});

const postMentoringCreate = http.post(MENTORING_URL, async ({ request }) => {
  const formData = await request.formData();

  const dataJson = formData.get('data');
  const image = formData.get('image');
  const certificateImages = formData.getAll('certificateImages');

  const parsedData = JSON.parse(dataJson as string);
  console.log(parsedData);
  console.log(image);
  console.log('certificateImages:', certificateImages);

  if (!parsedData) {
    return HttpResponse.json({ message: 'Bad Request' }, { status: 400 });
  }

  return HttpResponse.json({ message: true }, { status: 201 });
});

export const mentoringHandler = [getMentoringItems, postMentoringCreate];
