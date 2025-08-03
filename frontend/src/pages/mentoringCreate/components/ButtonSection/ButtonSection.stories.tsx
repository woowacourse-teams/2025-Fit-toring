import ButtonSection from './ButtonSection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/ButtonSection',
  component: ButtonSection,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof ButtonSection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultButtonSection: Story = {
  parameters: {
    docs: {
      description: {
        story:
          '멘토링 생성 페이지의 버튼 섹션입니다. "취소"와 "등록하기" 버튼이 포함되어 있습니다.',
      },
    },
  },
};
