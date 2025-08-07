import { ThemeProvider } from '@emotion/react';

import { THEME } from '../../../../common/styles/theme';

import Checkbox from './Checkbox';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'booking/Checkbox',
  component: Checkbox,

  decorators: [
    (Story) => (
      <ThemeProvider theme={THEME}>
        <Story />
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof Checkbox>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    checked: true,
    onChange: () => {},
    errored: false,
    id: 'test',
  },
  parameters: {
    docs: {
      description: {
        story: '기본 체크박스입니다.',
      },
    },
  },
};

export const Error: Story = {
  args: {
    checked: false,
    onChange: () => {},
    errored: true,
    id: 'test',
  },
  parameters: {
    docs: {
      description: {
        story: '에러 상태를 나타내는  체크박스입니다.',
      },
    },
  },
};
