import TitleSeparator from './TitleSeparator';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/TitleSeparator',
  component: TitleSeparator,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof TitleSeparator>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultTitleSeparator: Story = {
  args: {
    children: '기본 정보',
  },
  parameters: {
    docs: {
      description: {
        story:
          'TitleSeparator 컴포넌트는 멘토링 개설 페이지의 섹션 제목을 구성합니다.',
      },
    },
  },
};
