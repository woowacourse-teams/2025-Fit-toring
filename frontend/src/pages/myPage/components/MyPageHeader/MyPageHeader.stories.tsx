import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';

import MyPageHeader from './MyPageHeader';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'MyPage/MyPageHeader',
  component: MyPageHeader,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.MY_PAGE]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof MyPageHeader>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MyPageHeader 컴포넌트는 마이 페이지의 헤더를 구성합니다. 뒤로가기와 제목, 그리고 메뉴 버튼을 포함하고 있습니다.',
      },
    },
  },
};
