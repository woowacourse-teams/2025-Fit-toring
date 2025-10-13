import { http, HttpResponse } from 'msw';

const putImageUpload = http.put(
  'https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/fit-toring/*',
  async ({ request }) => {
    const data = await request.blob();

    if (!data) {
      return HttpResponse.json(
        { message: '이미지 업로드 실패' },
        { status: 400 },
      );
    }

    return HttpResponse.json({ message: true }, { status: 200 });
  },
);

export const imageUploadHandler = [putImageUpload];
