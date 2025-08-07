import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import { MENTORING_APPLICATIONS } from '../../../../common/mock/createdMentoring/data';
import MentoringApplicationItem from '../MentoringApplicationItem/MentoringApplicationItem';

import MentoringApplicationList from './MentoringApplicationList';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'CreatedMentoring/MentoringApplicationList',
  component: MentoringApplicationList,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.CREATED_MENTORING]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof MentoringApplicationList>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MentoringApplicationList 컴포넌트는 개설한 멘토링에 대해 받은 멘토링 상담 신청 목록을 보여주는 컴포넌트입니다. 멘토링 신청 정보를 세로로 나열하고 있습니다.',
      },
    },
  },
  args: {
    children: (
      <>
        {MENTORING_APPLICATIONS.map((item) => (
          <MentoringApplicationItem mentoringApplication={item} />
        ))}
      </>
    ),
  },
};
