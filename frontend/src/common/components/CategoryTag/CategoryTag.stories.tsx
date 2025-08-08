import CategoryTag from './CategoryTag';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'booking/CategoryTag',
  component: CategoryTag,
  decorators: [(Story) => <Story />],
} satisfies Meta<typeof CategoryTag>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    tagName: '카테고리 태그',
  },
  parameters: {
    docs: {
      description: {
        story: '기본 카테고리 태그입니다.',
      },
    },
  },
};
