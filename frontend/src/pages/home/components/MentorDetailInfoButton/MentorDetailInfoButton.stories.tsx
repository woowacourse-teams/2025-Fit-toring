import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';

import MentorDetailInfoButton from './MentorDetailInfoButton';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/MentorDetailInfoButton',
  component: MentorDetailInfoButton,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.HOME]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof MentorDetailInfoButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    id: 1,
  },
  parameters: {
    docs: {
      description: {
        story:
          'MentorDetailInfoButton 컴포넌트는 상세 정보 보기 버튼입니다. 상세 정보 보기 버튼 클릭 시 멘토 상세 정보 페이지로 이동합니다.',
      },
    },
  },
};
