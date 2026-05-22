import { http, HttpResponse } from 'msw';

import { API_ENDPOINTS } from '../../../common/constants/apiEndpoints';

import {
  COMMUNITY_POST_DETAIL,
  GUEST_POST_PASSWORD,
  POST_COMMENTS,
} from './data';

import type {
  PatchPostCommentRequest,
  PostComment,
  PostCommentRequest,
  PostCommentResponse,
} from '../types/postComment';

const BASE_URL = process.env.API_BASE_URL;
const COMMUNITY_POST_DETAIL_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId`;
const POST_OWNERSHIP_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/mine`;
const POST_COMMENTS_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/comments`;
const POST_LIKE_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/like`;
const POST_COMMENT_LIKE_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/comments/:commentId/like`;
const POST_COMMENT_OWNERSHIP_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/comments/mine`;
const GUEST_POST_COMMENTS_URL = `${BASE_URL}${API_ENDPOINTS.GUEST}${API_ENDPOINTS.POSTS}/:postId/comments`;
const GUEST_POST_CHECK_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId/guest-check`;
const DELETE_COMMUNITY_POST_URL = `${BASE_URL}${API_ENDPOINTS.POSTS}/:postId`;
const DELETE_GUEST_COMMUNITY_POST_URL = `${BASE_URL}${API_ENDPOINTS.GUEST}${API_ENDPOINTS.POSTS}/:postId`;
const COMMENT_URL = `${BASE_URL}${API_ENDPOINTS.COMMENTS}/:commentId`;
const GUEST_COMMENT_URL = `${BASE_URL}${API_ENDPOINTS.GUEST}${API_ENDPOINTS.COMMENTS}/:commentId`;

const toPostCommentResponse = (comment: PostComment): PostCommentResponse => {
  const { isMine, ...response } = comment;
  void isMine;

  return response;
};

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
  return HttpResponse.json(POST_COMMENTS.map(toPostCommentResponse));
});

const postCommunityPostLike = http.post(POST_LIKE_URL, async ({ params }) => {
  const postId = Number(params.postId);

  if (!COMMUNITY_POST_DETAIL.liked) {
    COMMUNITY_POST_DETAIL.liked = true;
    COMMUNITY_POST_DETAIL.likeCount += 1;
  }

  return HttpResponse.json({
    postId,
    liked: COMMUNITY_POST_DETAIL.liked,
    likeCount: COMMUNITY_POST_DETAIL.likeCount,
  });
});

const deleteCommunityPostLike = http.delete(
  POST_LIKE_URL,
  async ({ params }) => {
    const postId = Number(params.postId);

    if (COMMUNITY_POST_DETAIL.liked) {
      COMMUNITY_POST_DETAIL.liked = false;
      COMMUNITY_POST_DETAIL.likeCount = Math.max(
        0,
        COMMUNITY_POST_DETAIL.likeCount - 1,
      );
    }

    return HttpResponse.json({
      postId,
      liked: COMMUNITY_POST_DETAIL.liked,
      likeCount: COMMUNITY_POST_DETAIL.likeCount,
    });
  },
);

const postCommunityPostCommentLike = http.post(
  POST_COMMENT_LIKE_URL,
  async ({ params }) => {
    const commentId = Number(params.commentId);
    const targetComment = POST_COMMENTS.find(({ id }) => id === commentId);

    if (!targetComment || targetComment.isDeleted) {
      return HttpResponse.json(
        { message: '댓글을 찾을 수 없습니다.' },
        { status: 404 },
      );
    }

    if (!targetComment.liked) {
      targetComment.liked = true;
      targetComment.likeCount += 1;
    }

    return HttpResponse.json({
      commentId,
      liked: targetComment.liked,
      likeCount: targetComment.likeCount,
    });
  },
);

const deleteCommunityPostCommentLike = http.delete(
  POST_COMMENT_LIKE_URL,
  async ({ params }) => {
    const commentId = Number(params.commentId);
    const targetComment = POST_COMMENTS.find(({ id }) => id === commentId);

    if (!targetComment || targetComment.isDeleted) {
      return HttpResponse.json(
        { message: '댓글을 찾을 수 없습니다.' },
        { status: 404 },
      );
    }

    if (targetComment.liked) {
      targetComment.liked = false;
      targetComment.likeCount = Math.max(0, targetComment.likeCount - 1);
    }

    return HttpResponse.json({
      commentId,
      liked: targetComment.liked,
      likeCount: targetComment.likeCount,
    });
  },
);

const getCommunityPostOwnership = http.get(POST_OWNERSHIP_URL, async () => {
  return HttpResponse.json({ isMine: true });
});

const getCommunityPostCommentOwnership = http.get(
  POST_COMMENT_OWNERSHIP_URL,
  async () => {
    return HttpResponse.json({
      mineCommentIds: POST_COMMENTS.filter(
        ({ isDeleted, isMine }) => isMine && !isDeleted,
      ).map(({ id }) => id),
    });
  },
);

let nextCommentId = Math.max(...POST_COMMENTS.map(({ id }) => id)) + 1;

const createPostCommentResponse = async (
  request: Request,
  isGuestComment: boolean,
) => {
  const requestBody = (await request.json()) as PostCommentRequest;

  const newComment = {
    id: nextCommentId++,
    content: requestBody.content,
    nickname:
      requestBody.nickname ?? (requestBody.isAnonymous ? '익명' : '작성자명'),
    isAnonymous: requestBody.isAnonymous ?? false,
    isGuestComment: Boolean(requestBody.guestPassword),
    isMine: !requestBody.isAnonymous && !requestBody.guestPassword,
    rootId: requestBody.rootId,
    parentId: requestBody.parentId,
    isDeleted: false,
    createdAt: new Date().toISOString(),
    likeCount: 0,
    liked: false,
  };

  POST_COMMENTS.push(newComment);
  COMMUNITY_POST_DETAIL.commentCount += 1;

  return HttpResponse.json(toPostCommentResponse(newComment), { status: 201 });
};

const postPostComment = http.post(POST_COMMENTS_URL, async ({ request }) =>
  createPostCommentResponse(request, false),
);

const postGuestPostComment = http.post(
  GUEST_POST_COMMENTS_URL,
  async ({ request }) => createPostCommentResponse(request, true),
);

const patchPostComment = http.patch(
  COMMENT_URL,
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

    if (targetComment.isGuestComment || !targetComment.isMine) {
      return HttpResponse.json(
        { message: '댓글 수정 권한이 없습니다.' },
        { status: 403 },
      );
    }

    targetComment.content = requestBody.content;

    return new HttpResponse(null, { status: 200 });
  },
);

const patchGuestPostComment = http.patch(
  GUEST_COMMENT_URL,
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
      !targetComment.isGuestComment ||
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

const deletePostComment = http.delete(COMMENT_URL, async ({ params }) => {
  const { commentId } = params;
  const targetComment = POST_COMMENTS.find(
    ({ id }) => id === Number(commentId),
  );

  if (!targetComment) {
    return HttpResponse.json(
      { message: '댓글을 찾을 수 없습니다.' },
      { status: 404 },
    );
  }

  if (targetComment.isGuestComment || !targetComment.isMine) {
    return HttpResponse.json(
      { message: '댓글 삭제 권한이 없습니다.' },
      { status: 403 },
    );
  }

  targetComment.isDeleted = true;

  return new HttpResponse(null, { status: 204 });
});

const deleteGuestPostComment = http.delete(
  GUEST_COMMENT_URL,
  async ({ params, request }) => {
    const { commentId } = params;
    const requestBody = (await request.json().catch(() => null)) as {
      guestPassword?: string;
    } | null;
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
      !targetComment.isGuestComment ||
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

const postCommunityPostCommentGuestCheck = http.post(
  `${BASE_URL}${API_ENDPOINTS.COMMENTS}/:commentId/pw-check`,
  async ({ params, request }) => {
    const { commentId } = params;
    const requestBody = (await request.json()) as { guestPassword: string };
    const targetComment = POST_COMMENTS.find(
      ({ id }) => id === Number(commentId),
    );

    if (!targetComment) {
      return HttpResponse.json(
        { message: '댓글을 찾을 수 없습니다.' },
        { status: 404 },
      );
    }

    if (!targetComment.isGuestComment) {
      return new HttpResponse(null, { status: 200 });
    }

    if (requestBody.guestPassword !== GUEST_POST_PASSWORD) {
      return HttpResponse.json(
        { message: '비밀번호가 일치하지 않습니다.' },
        { status: 400 },
      );
    }

    return new HttpResponse(null, { status: 200 });
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

const deleteCommunityPost = http.delete(DELETE_COMMUNITY_POST_URL, async () => {
  return new HttpResponse(null, { status: 204 });
});

const deleteGuestCommunityPost = http.delete(
  DELETE_GUEST_COMMUNITY_POST_URL,
  async ({ request }) => {
    const requestBody = (await request.json().catch(() => null)) as {
      guestPassword?: string;
    } | null;

    if (requestBody?.guestPassword !== GUEST_POST_PASSWORD) {
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
  getCommunityPostOwnership,
  getCommunityPostCommentOwnership,
  getPostComments,
  postCommunityPostLike,
  deleteCommunityPostLike,
  postCommunityPostCommentLike,
  deleteCommunityPostCommentLike,
  postPostComment,
  postGuestPostComment,
  patchPostComment,
  patchGuestPostComment,
  deletePostComment,
  deleteGuestPostComment,
  postCommunityPostCommentGuestCheck,
  postGuestPostCheck,
  deleteCommunityPost,
  deleteGuestCommunityPost,
];
