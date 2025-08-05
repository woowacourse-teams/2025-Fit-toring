import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';

import MyProfile from './MyProfile';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'MyPage/MyProfile',
  component: MyProfile,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.MY_PAGE]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof MyProfile>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MyProfile 컴포넌트는 마이 페이지의 프로필 정보를 표시합니다. 프로필 사진, 사용자 이름, 아이디, 전화번호를 포함하고 있습니다.',
      },
    },
  },
};
