import { MemoryRouter } from 'react-router-dom';

import { USER_PROFILE } from '../../../common/mock/getUserInfo/data';

import EditProfileForm from './EditProfileForm';

import type { Meta, StoryObj } from '@storybook/react';

const meta = {
  title: 'editProfile/EditProfileForm',
  component: EditProfileForm,
  decorators: [
    (Story) => (
      <MemoryRouter>
        <Story />
      </MemoryRouter>
    ),
  ],

  args: {
    myProfile: USER_PROFILE,
  },
} satisfies Meta<typeof EditProfileForm>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          '회원정보 수정 페이지의 폼입니다. 초기 프로필 데이터가 주입됩니다.',
      },
    },
  },
};
