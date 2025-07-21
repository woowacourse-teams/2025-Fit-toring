import { ThemeProvider } from '@emotion/react';
import { BrowserRouter } from 'react-router-dom';

import { THEME } from '../../../../common/styles/theme';

import BookingHeader from './BookingHeader';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Booking/BookingHeader',
  component: BookingHeader,
  parameters: {},

  tags: ['autodocs'],
  decorators: [
    (Story) => (
      <BrowserRouter>
        <ThemeProvider theme={THEME}>
          <Story />
        </ThemeProvider>
      </BrowserRouter>
    ),
  ],
} satisfies Meta<typeof BookingHeader>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '헤더입니다.',
      },
    },
  },
};
