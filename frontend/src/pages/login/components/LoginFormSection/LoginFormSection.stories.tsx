import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import LoginFormSection from '../LoginFormSection/LoginFormSection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'login/LoginFormSection',
  component: LoginFormSection,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.LOGIN]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof LoginFormSection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '로그인 페이지의 로그인 폼 영역입니다.',
      },
    },
  },
};
