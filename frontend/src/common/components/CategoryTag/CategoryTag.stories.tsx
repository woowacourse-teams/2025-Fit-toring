import { ThemeProvider } from '@emotion/react';

import { THEME } from '../../styles/theme';

import CategoryTag from './CategoryTag';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'booking/CategoryTag',
  component: CategoryTag,
  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <ThemeProvider theme={THEME}>
        <Story />
      </ThemeProvider>
    ),
  ],
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
