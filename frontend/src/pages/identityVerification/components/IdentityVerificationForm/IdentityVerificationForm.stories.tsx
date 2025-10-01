import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';

import type { Meta, StoryObj } from '@storybook/react-webpack5';
import IdentityVerificationForm from './IdentityVerificationForm';

const meta = {
  title: 'identityVerification/IdentityVerificationForm',
  component: IdentityVerificationForm,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.MENTORING_CREATE]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof IdentityVerificationForm>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultIdentityVerificationForm: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'IdentityVerificationForm 컴포넌트는 본인 인증 페이지의 메인 폼을 구성합니다.',
      },
    },
  },
};
