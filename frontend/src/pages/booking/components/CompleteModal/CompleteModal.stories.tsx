import { MemoryRouter } from 'react-router-dom';
import { fn } from 'storybook/test';

import { PAGE_URL } from '../../../../common/constants/url';

import CompleteModal from './CompleteModal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Booking/CompleteModal',
  component: CompleteModal,

  args: { opened: true, onCloseClick: fn() },
  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.BOOKING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof CompleteModal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultModal: Story = {
  args: {
    mentorInfo: {
      id: 10,
      mentorName: '김트레이너',
      categories: ['근력 강화', '다이어트', '벌크업'],
      price: 0,
      career: 2,
      profileImageUrl: '',
      introduction:
        '안녕하세요. 김트레이너입니다. 현재 이벤트로 무료 상담하고 있습니다.',
      content:
        '현재 무료 상담 진행하고 있습니다. \n\n언제든 편하게 연락주시면 빠르게 답장하겠습니다.',
      chatUrl: '',
      certificates: [
        {
          certificateId: 19,
          title: '핏토링',
          type: 'LICENSE',
          imageUrl:
            'https://techcourse-project-2025.s3.amazonaws.com/fit-toring/certificate-image/d2dab999-8add-4450-91f3-499765d647cb.svg',
        },
      ],
      ratingAverage: '0.0',
      ratingCount: 0,
    },
    opened: true,
    onCloseClick: fn(),
  },
  parameters: {
    docs: {
      description: {
        story: `예약 완료 모달입니다. 사용자가 예약을 완료하면 이 모달이 표시되고 멘토의 번호를 확인할 수 있습니다.`,
      },
    },
  },
};
