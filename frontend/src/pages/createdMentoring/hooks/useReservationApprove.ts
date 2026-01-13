import { useMutation } from '@tanstack/react-query';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { patchReservationApprove } from '../apis/patchReservationApprove';

const useReservationApprove = (onSuccess: () => Promise<void>) => {
  const mutation = useMutation({
    mutationFn: async (reservationId: number) => {
      await patchReservationApprove(reservationId);
    },
    onSuccess,
    onError: (error) => {
      console.error(`Error handling approve button click:`, error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'createdMentoring',
        step: 'approve-button-click',
      });
    },
  });

  return mutation;
};

export default useReservationApprove;
