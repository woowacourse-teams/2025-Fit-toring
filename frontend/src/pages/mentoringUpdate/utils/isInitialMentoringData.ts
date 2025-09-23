import type { MentoringUpdateFormData } from '../types/mentoringUpdateForm';

export const INITIAL_UPDATE_MENTORING_DATA = {
  price: 0,
  category: [],
  introduction: '',
  career: 0,
  content: '',
  profileImageUrl: '',
  chatUrl: '',
  certificateInfos: [
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
    data.chatUrl === INITIAL_UPDATE_MENTORING_DATA.chatUrl &&
    data.certificateInfos.length === 1 &&
    data.certificateInfos[0].id ===
      INITIAL_UPDATE_MENTORING_DATA.certificateInfos[0].id &&
    data.certificateInfos[0].title ===
      INITIAL_UPDATE_MENTORING_DATA.certificateInfos[0].title &&
    data.certificateInfos[0].type ===
      INITIAL_UPDATE_MENTORING_DATA.certificateInfos[0].type
  );
};
