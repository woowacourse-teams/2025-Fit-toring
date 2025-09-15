import SortButton from './SortButton';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/SortButton',
  component: SortButton,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof SortButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    handleSortButtonClick: () => {},
  },
  parameters: {
    docs: {
      description: {
        story: 'SortButton 컴포넌트는 멘토 리스트를 정렬하는 버튼입니다.',
      },
    },
  },
};
