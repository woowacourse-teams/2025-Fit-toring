import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import {
  COMMUNITY_POST_DETAIL,
  GUEST_POST_PASSWORD,
  POST_COMMENTS,
} from './data';

import type {
  PatchPostCommentRequest,
  PostCommentRequest,
} from '../types/postComment';

const BASE_URL = process.env.API_BASE_URL;
const COMMUNITY_POST_DETAIL_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId`;
const POST_COMMENTS_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/comments`;
const GUEST_POST_CHECK_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/guest-check`;
const DELETE_COMMUNITY_POST_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId`;

const getCommunityPostDetail = http.get(
  COMMUNITY_POST_DETAIL_URL,
  async ({ params }) => {
    const { postId } = params;

    return HttpResponse.json({
      ...COMMUNITY_POST_DETAIL,
      id: Number(postId),
    });
  },
);

const getPostComments = http.get(POST_COMMENTS_URL, async () => {
  return HttpResponse.json(POST_COMMENTS);
});

let nextCommentId = Math.max(...POST_COMMENTS.map(({ id }) => id)) + 1;

const postPostComment = http.post(POST_COMMENTS_URL, async ({ request }) => {
  const requestBody = (await request.json()) as PostCommentRequest;

  const newComment = {
    id: nextCommentId++,
    content: requestBody.content,
    nickname:
      requestBody.nickname ??
      (requestBody.isAnonymous ? '익명' : '작성자명'),
    isAnonymous: requestBody.isAnonymous ?? false,
    isGuestComment: Boolean(requestBody.guestPassword),
    rootId: requestBody.rootId,
    parentId: requestBody.parentId,
    isDeleted: false,
    createdAt: new Date().toISOString(),
  };

  POST_COMMENTS.push(newComment);
  COMMUNITY_POST_DETAIL.commentCount += 1;

  return HttpResponse.json(newComment, { status: 201 });
});

const patchPostComment = http.patch(
  `${BASE_URL}${API_ENDPOINTS.COMMENTS}/:commentId`,
  async ({ params, request }) => {
    const { commentId } = params;
    const requestBody = (await request.json()) as PatchPostCommentRequest;
    const targetComment = POST_COMMENTS.find(
      ({ id }) => id === Number(commentId),
    );

    if (!targetComment) {
      return HttpResponse.json(
        { message: '댓글을 찾을 수 없습니다.' },
        { status: 404 },
      );
    }

    if (requestBody.content.trim() === '') {
      return HttpResponse.json(
        { message: '수정할 내용을 입력해주세요.' },
        { status: 400 },
      );
    }

    if (
      targetComment.isGuestComment &&
      requestBody.guestPassword !== GUEST_POST_PASSWORD
    ) {
      return HttpResponse.json(
        { message: '비밀번호가 일치하지 않습니다.' },
        { status: 400 },
      );
    }

    targetComment.content = requestBody.content;

    return new HttpResponse(null, { status: 200 });
  },
);

const deletePostComment = http.delete(
  `${BASE_URL}${API_ENDPOINTS.COMMENTS}/:commentId`,
  async ({ params, request }) => {
    const { commentId } = params;
    const requestBody = (await request.json().catch(() => null)) as
      | { guestPassword?: string }
      | null;
    const targetComment = POST_COMMENTS.find(
      ({ id }) => id === Number(commentId),
    );

    if (!targetComment) {
      return HttpResponse.json(
        { message: '댓글을 찾을 수 없습니다.' },
        { status: 404 },
      );
    }

    if (
      targetComment.isGuestComment &&
      requestBody?.guestPassword !== GUEST_POST_PASSWORD
    ) {
      return HttpResponse.json(
        { message: '비밀번호가 일치하지 않습니다.' },
        { status: 400 },
      );
    }

    targetComment.isDeleted = true;

    return new HttpResponse(null, { status: 204 });
  },
);

const postGuestPostCheck = http.post(
  GUEST_POST_CHECK_URL,
  async ({ request }) => {
    const requestBody = (await request.json()) as { guestPassword: string };

    if (requestBody.guestPassword !== GUEST_POST_PASSWORD) {
      return HttpResponse.json(
        { message: '비밀번호가 일치하지 않습니다.' },
        { status: 400 },
      );
    }

    return new HttpResponse(null, { status: 200 });
  },
);

const deleteCommunityPost = http.delete(
  DELETE_COMMUNITY_POST_URL,
  async ({ request }) => {
    const requestBody = (await request.json().catch(() => null)) as
      | { guestPassword?: string }
      | null;

    if (
      COMMUNITY_POST_DETAIL.isGuestPost &&
      requestBody?.guestPassword !== GUEST_POST_PASSWORD
    ) {
      return HttpResponse.json(
        { message: '비밀번호가 일치하지 않습니다.' },
        { status: 400 },
      );
    }

    return new HttpResponse(null, { status: 204 });
  },
);

export const communityPostDetailHandler = [
  getCommunityPostDetail,
  getPostComments,
  postPostComment,
  patchPostComment,
  deletePostComment,
  postGuestPostCheck,
  deleteCommunityPost,
];
