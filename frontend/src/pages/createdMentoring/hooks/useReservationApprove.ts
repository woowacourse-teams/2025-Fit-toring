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
      console.error('예약 승인 처리 중 오류가 발생했습니다.', error);
      alert(
        error.message ||
          '예약 승인 처리 중 오류가 발생했습니다. 다시 시도해주세요.',
      );
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
