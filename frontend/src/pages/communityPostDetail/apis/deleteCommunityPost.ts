import { apiClient } from '../../../common/apis/apiClient';

interface DeleteCommunityPostParams {
  postId: string;
  guestPassword?: string;
}

export const deleteCommunityPost = async ({
  postId,
  guestPassword,
}: DeleteCommunityPostParams) => {
  await apiClient.delete({
    endpoint: `/posts/${postId}`,
    body: guestPassword ? { guestPassword } : undefined,
    withCredentials: true,
  });
};
