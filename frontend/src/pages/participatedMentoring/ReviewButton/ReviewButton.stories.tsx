import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../common/constants/url';
import { StatusTypeEnum } from '../../../common/types/statusType';

import ReviewButton from './ReviewButton';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'ParticipatedMentoring/ReviewButton',
  component: ReviewButton,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.PARTICIPATED_MENTORING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof ReviewButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    isReviewed: false,
    status: StatusTypeEnum.completed,
  },
  parameters: {
    docs: {
      description: {
        story:
          'ReviewButton 컴포넌트는 신청 및 리뷰 작성 상태에 따라 리뷰 작성 혹은 리뷰 완료 버튼을 표시합니다. 상태에 따라 텍스트와 스타일이 변경됩니다.',
      },
    },
  },
};

export const CanReview: Story = {
  args: {
    isReviewed: false,
    status: StatusTypeEnum.completed,
  },
  parameters: {
    docs: {
      description: {
        story:
          '상담이 완료된 상태이고, 리뷰 작성 전이면 리뷰 작성이 가능합니다.',
      },
    },
  },
};

export const ReviewCompleted: Story = {
  args: {
    isReviewed: true,
    status: StatusTypeEnum.completed,
  },
  parameters: {
    docs: {
      description: {
        story:
          '상담이 완료된 상태이고 리뷰를 작성한 리뷰 버튼입니다. 리뷰 완료 버튼이 표시됩니다.',
      },
    },
  },
};

export const CanNotReview: Story = {
  args: {
    isReviewed: false,
    status: StatusTypeEnum.pending,
  },
  parameters: {
    docs: {
      description: {
        story:
          '리뷰 작성이 불가능한 리뷰 버튼입니다. 리뷰가 되었거나, 상태가 "완료됨"이 아니면 리뷰 작성 버튼이 disabled 처리됩니다.',
      },
    },
  },
};
