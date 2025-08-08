import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../common/constants/url';
import { PARTICIPATED_MENTORING_LIST } from '../ParticipatedMentoring';

import MentoringItem from './MentoringItem';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'ParticipatedMentoring/MentoringItem',
  component: MentoringItem,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.PARTICIPATED_MENTORING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof MentoringItem>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    mentoring: PARTICIPATED_MENTORING_LIST[0],
  },
  parameters: {
    docs: {
      description: {
        story:
          'MentoringItem 컴포넌트는 내가 신청한 멘토링 정보 카드입니다. 멘토링 상담 신청 정보를 보여주고, 상태에 따라 리뷰 작성 버튼을 표시합니다. 상태에 따라 아이콘과 스타일이 변경됩니다.',
      },
    },
  },
};

export const Pending: Story = {
  args: {
    mentoring: PARTICIPATED_MENTORING_LIST[2],
  },
  parameters: {
    docs: {
      description: {
        story:
          '승인 대기중인 멘토링 상담 신청 정보 카드입니다. 상태가 "승인대기"으로 표시되며, 승인 대기중인 상담 신청 정보를 보여줍니다.',
      },
    },
  },
};

export const Approved: Story = {
  args: {
    mentoring: PARTICIPATED_MENTORING_LIST[1],
  },
  parameters: {
    docs: {
      description: {
        story:
          '승인된 멘토링 상담 신청 정보 카드입니다. 상태가 "승인됨"으로 표시되며, 승인된 상담 신청 정보를 보여줍니다.',
      },
    },
  },
};

export const Completed: Story = {
  args: {
    mentoring: PARTICIPATED_MENTORING_LIST[0],
  },
  parameters: {
    docs: {
      description: {
        story:
          '완료된 멘토링 상담 신청 정보 카드입니다. 상태가 "완료됨"으로 표시되며, 리뷰 작성을 하지 않았다면 리뷰 작성 버튼이 표시됩니다. 완료된 상담 신청 정보를 보여줍니다.',
      },
    },
  },
};
