import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';

import HomeHeader from './HomeHeader';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/HomeHeader',
  component: HomeHeader,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.HOME]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof HomeHeader>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'HomeHeader 컴포넌트는 홈 페이지의 헤더를 구성합니다. 로고와 제목을 포함하고 있습니다. 로고 클릭 시 메인 홈 페이지로 이동합니다.',
      },
    },
  },
};
