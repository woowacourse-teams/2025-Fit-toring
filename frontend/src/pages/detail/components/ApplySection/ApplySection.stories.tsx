import { BrowserRouter } from 'react-router-dom';

import ApplySection from './ApplySection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/ApplySection',
  component: ApplySection,

  decorators: [
    (Story) => (
      <BrowserRouter>
        <Story />
      </BrowserRouter>
    ),
  ],
} satisfies Meta<typeof ApplySection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    price: 5000,
    mentoringId: '1',
  },
  parameters: {
    docs: {
      description: {
        story: `상담 신청 섹션입니다. 사용자가 상담을 신청할 수 있는 버튼과 상담료 정보를 표시합니다.`,
      },
    },
  },
};
