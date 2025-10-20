import SortDropDown from './SortDropDown';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/SortDropDown',
  component: SortDropDown,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof SortDropDown>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    handleSortButtonClick: () => {},
    currentSortKey: 'CREATED_AT',
  },
  parameters: {
    docs: {
      description: {
        story:
          'SortDropDown 컴포넌트는 멘토 리스트를 정렬하는 버튼들이 보이는 드롭다운입니다.',
      },
    },
  },
};
