import { http, HttpResponse } from 'msw';
import { describe, expect, it } from 'vitest';

import { API_ENDPOINTS } from '../src/common/constants/apiEndpoints';
import { server } from '../src/common/mock/server';
import {
  getCommunityPostOwnership,
} from '../src/pages/communityPostDetail/apis/getCommunityPostOwnership';

const POST_OWNERSHIP_URL =
  `${process.env.API_BASE_URL}${API_ENDPOINTS.POSTS}/:postId/mine`;

describe('getCommunityPostOwnership API', () => {
  it('게시글 소유권 조회 결과가 true이면 isMine true를 반환한다.', async () => {
    const response = await getCommunityPostOwnership('1');

    expect(response).toEqual({ isMine: true });
  });

  it('게시글 소유권 조회 결과가 false이면 isMine false를 반환한다.', async () => {
    server.use(
      http.get(POST_OWNERSHIP_URL, () => {
        return HttpResponse.json({ isMine: false });
      }),
    );

    const response = await getCommunityPostOwnership('1');

    expect(response).toEqual({ isMine: false });
  });
});
