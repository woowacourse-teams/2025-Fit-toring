import { useMutation } from '@tanstack/react-query';

import { captureSentryError } from '../../../common/utils/captureSentryError';
import { patchReservationReject } from '../apis/patchReservationReject';

const useReservationReject = (onSuccess: () => Promise<void>) => {
  const mutation = useMutation({
    mutationFn: async (reservationId: number) => {
      await patchReservationReject(reservationId);
    },
    onSuccess,
    onError: (error) => {
      console.error(`Error handling reject button click:`, error);
      alert(
        error.message ||
          '예약 거절 처리 중 오류가 발생했습니다. 다시 시도해주세요.',
      );
      captureSentryError({
        error,
        level: 'warning',
        feature: 'createdMentoring',
        step: 'reject-button-click',
      });
    },
  });

  return mutation;
};

export default useReservationReject;
