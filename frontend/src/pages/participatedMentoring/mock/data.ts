import { StatusTypeEnum } from '../../../common/types/statusType';

import type { ParticipatedMentoringType } from '../types/participatedMentoring';

export const PARTICIPATED_MENTORING_LIST: ParticipatedMentoringType[] = [
  {
    reservationId: 1,
    mentoringId: 1,
    mentorName: '이수업',
    mentorProfileImage:
      'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.crowdpic.net%2Fphoto%2F%25ED%2592%258D%25EA%25B2%25BD-%25EC%259E%2590%25EC%2597%25B0-%25EB%2593%25A4%25ED%258C%2590-%25EC%25B4%2588%25EC%259B%2590-%25EB%2582%2598%25EB%25AC%25B4-136857%3Fsrsltid%3DAfmBOopK3sXndcC9IZQXkHBG_NVOu_ZELeaPahtqFY0gMkGhB9zkxqBr&psig=AOvVaw2i3WaVGdyiSKf3VAcV-BK2&ust=1754548076306000&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCODkj4rH9Y4DFQAAAAAdAAAAABAE',
    content: '상체 근력 운동 궁금해요',
    reservedAt: '2024-01-09',
    isReviewed: true,
    status: StatusTypeEnum.COMPLETE,
  },
  {
    reservationId: 2,
    mentoringId: 2,

    mentorName: '김트레이너',
    mentorProfileImage:
      'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.crowdpic.net%2Fphoto%2F%25ED%2592%258D%25EA%25B2%25BD-%25EC%259E%2590%25EC%2597%25B0-%25EB%2593%25A4%25ED%258C%2590-%25EC%25B4%2588%25EC%259B%2590-%25EB%2582%2598%25EB%25AC%25B4-136857%3Fsrsltid%3DAfmBOopK3sXndcC9IZQXkHBG_NVOu_ZELeaPahtqFY0gMkGhB9zkxqBr&psig=AOvVaw2i3WaVGdyiSKf3VAcV-BK2&ust=1754548076306000&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCODkj4rH9Y4DFQAAAAAdAAAAABAE',
    content: '상체 근력 운동 궁금해요',
    reservedAt: '2024-01-10',
    isReviewed: false,
    status: StatusTypeEnum.COMPLETE,
  },
  {
    reservationId: 3,
    mentoringId: 3,
    mentorName: '박코치',
    mentorProfileImage:
      'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.crowdpic.net%2Fphoto%2F%25ED%2592%258D%25EA%25B2%25BD-%25EC%259E%2590%25EC%2597%25B0-%25EB%2593%25A4%25ED%258C%2590-%25EC%25B4%2588%25EC%259B%2590-%25EB%2582%2598%25EB%25AC%25B4-136857%3Fsrsltid%3DAfmBOopK3sXndcC9IZQXkHBG_NVOu_ZELeaPahtqFY0gMkGhB9zkxqBr&psig=AOvVaw2i3WaVGdyiSKf3VAcV-BK2&ust=1754548076306000&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCODkj4rH9Y4DFQAAAAAdAAAAABAE',
    content: '상체 근력 운동 궁금해요',

    reservedAt: '2024-01-12',
    isReviewed: false,
    status: StatusTypeEnum.APPROVED,
  },
  {
    reservationId: 4,
    mentoringId: 4,
    mentorName: '이헬퍼',
    mentorProfileImage:
      'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.crowdpic.net%2Fphoto%2F%25ED%2592%258D%25EA%25B2%25BD-%25EC%259E%2590%25EC%2597%25B0-%25EB%2593%25A4%25ED%258C%2590-%25EC%25B4%2588%25EC%259B%2590-%25EB%2582%2598%25EB%25AC%25B4-136857%3Fsrsltid%3DAfmBOopK3sXndcC9IZQXkHBG_NVOu_ZELeaPahtqFY0gMkGhB9zkxqBr&psig=AOvVaw2i3WaVGdyiSKf3VAcV-BK2&ust=1754548076306000&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCODkj4rH9Y4DFQAAAAAdAAAAABAE',
    content: '상체 근력 운동 궁금해요',

    reservedAt: '2024-01-15',
    isReviewed: false,
    status: StatusTypeEnum.PENDING,
  },
];
