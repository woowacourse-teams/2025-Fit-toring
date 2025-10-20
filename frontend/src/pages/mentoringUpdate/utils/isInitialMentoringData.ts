import type { MentoringUpdateFormData } from '../types/mentoringUpdateForm';

export const INITIAL_UPDATE_MENTORING_DATA = {
  price: 0,
  category: [],
  introduction: '',
  career: 0,
  content: '',
  profileImageUrl: '',
  certificateInfoRequests: [
    {
      id: '0',
      title: null,
      type: null,
    },
  ],
};

export const isInitialMentoringData = (data: MentoringUpdateFormData) => {
  return (
    data.price === INITIAL_UPDATE_MENTORING_DATA.price &&
    data.category.length === 0 &&
    data.introduction === INITIAL_UPDATE_MENTORING_DATA.introduction &&
    data.career === INITIAL_UPDATE_MENTORING_DATA.career &&
    data.content === INITIAL_UPDATE_MENTORING_DATA.content &&
    data.certificateInfoRequests.length === 1 &&
    data.certificateInfoRequests[0].id ===
      INITIAL_UPDATE_MENTORING_DATA.certificateInfoRequests[0].id &&
    data.certificateInfoRequests[0].title ===
      INITIAL_UPDATE_MENTORING_DATA.certificateInfoRequests[0].title &&
    data.certificateInfoRequests[0].type ===
      INITIAL_UPDATE_MENTORING_DATA.certificateInfoRequests[0].type
  );
};
