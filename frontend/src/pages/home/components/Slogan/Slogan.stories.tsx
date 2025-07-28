import Slogan from './Slogan';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/Slogan',
  component: Slogan,

  tags: ['autodocs'],

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof Slogan>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'Slogan 컴포넌트는 홈페이지의 상단에 위치하여 사용자에게 핏토링의 핵심가치를 전달합니다.',
      },
    },
  },
};
