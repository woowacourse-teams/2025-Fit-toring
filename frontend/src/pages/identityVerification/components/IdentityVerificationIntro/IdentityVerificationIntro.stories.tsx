import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';

import IdentityVerificationIntro from './IdentityVerificationIntro';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'identityVerification/IdentityVerificationIntro',
  component: IdentityVerificationIntro,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.MENTORING_CREATE]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof IdentityVerificationIntro>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultIdentityVerificationIntro: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'IdentityVerificationIntro 컴포넌트는 본인 인증 페이지의 소개 섹션을 구성합니다.',
      },
    },
  },
};
