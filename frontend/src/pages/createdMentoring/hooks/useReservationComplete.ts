import { useMutation } from '@tanstack/react-query';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { patchReservationComplete } from '../apis/patchReservationComplete';

const useReservationComplete = (onSuccess: () => Promise<void>) => {
  const mutation = useMutation({
    mutationFn: async (reservationId: number) => {
      await patchReservationComplete(reservationId);
    },
    onSuccess,
    onError: (error) => {
      console.error(`Error handling complete button click:`, error);
      captureSentryError({
        error,
        level: 'warning',
        feature: 'createdMentoring',
        step: 'complete-button-click',
      });
    },
  });

  return mutation;
};

export default useReservationComplete;
