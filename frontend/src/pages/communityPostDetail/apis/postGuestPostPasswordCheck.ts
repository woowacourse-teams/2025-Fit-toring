import { apiClient } from '../../../common/apis/apiClient';

interface PostGuestPostPasswordCheckParams {
  postId: string;
  guestPassword: string;
}

export const postGuestPostPasswordCheck = async ({
  postId,
  guestPassword,
}: PostGuestPostPasswordCheckParams) => {
  await apiClient.post({
    endpoint: `/posts/${postId}/guest-check`,
    body: { guestPassword },
  });

  return true;
};
