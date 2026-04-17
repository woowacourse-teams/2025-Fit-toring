import type { PostComment } from '../types/postComment';

interface PostCommentNode extends PostComment {
  children: PostCommentNode[];
}

export const buildCommentTree = (commentData: PostComment[]) => {
  const commentNodeMap = new Map<number, PostCommentNode>(
    commentData.map((comment) => [comment.id, { ...comment, children: [] }]),
  );

  const rootComments: PostCommentNode[] = [];

  commentData.forEach((comment) => {
    const currentComment = commentNodeMap.get(comment.id);

    if (!currentComment) {
      return;
    }

    if (comment.parentId === null) {
      rootComments.push(currentComment);
      return;
    }

    const parentComment = commentNodeMap.get(comment.parentId);

    if (!parentComment) {
      rootComments.push(currentComment);
      return;
    }

    parentComment.children.push(currentComment);
  });

  return rootComments;
};
