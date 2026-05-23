import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../constants/apiEndpoints';

const BASE_URL = process.env.API_BASE_URL;
const PRESIGNED_URL = `${BASE_URL}${API_ENDPOINTS.REQUEST_PRESIGNED_URL}`;

const IMAGE_TYPE_DIR = {
  MEMBER_PROFILE: 'member-profile-image',
  MENTORING_PROFILE: 'profile-image',
  CERTIFICATE: 'certificate-image',
  CHAT: 'chat-image',
} as const;

const postPresignedUrl = http.post(PRESIGNED_URL, async ({ request }) => {
  const body = (await request.json()) as {
    imageType?: string;
    extension?: string;
  };

  if (
    !body.imageType ||
    !(body.imageType in IMAGE_TYPE_DIR) ||
    !body.extension
  ) {
    return HttpResponse.json(
      { message: 'Presigned URL 발급 실패' },
      { status: 400 },
    );
  }

  const imageType = body.imageType as keyof typeof IMAGE_TYPE_DIR;
  const key = `fit-toring/local/${IMAGE_TYPE_DIR[imageType]}/default/mock-image.${body.extension}`;

  return HttpResponse.json(
    {
      presignedUrl: `https://techcourse-project-2025.s3.ap-northeast-2.amazonaws.com/${key}?signature=mock`,
      key,
      expiresAt: new Date(Date.now() + 3 * 60 * 1000).toISOString(),
    },
    { status: 201 },
  );
});

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

export const imageUploadHandler = [postPresignedUrl, putImageUpload];
