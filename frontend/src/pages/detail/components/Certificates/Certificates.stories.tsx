import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import { MENTORING_DETAIL } from '../../../../common/mock/getUserInfoSummary/data';

import Certificates from './Certificates';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/Certificates',
  component: Certificates,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.DETAIL]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof Certificates>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    certificates: MENTORING_DETAIL.certificates,
  },
  parameters: {
    docs: {
      description: {
        story: `자격증 정보 섹션입니다. 사용자가 보유한 자격증 정보를 확인할 수 있습니다.`,
      },
    },
  },
};
