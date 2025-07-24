import { ThemeProvider } from '@emotion/react';
import { MemoryRouter } from 'react-router-dom';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { PAGE_URL } from '../../../../common/constants/url';
import { THEME } from '../../../../common/styles/theme';
import MentorCardItem from '../MentorCardItem/MentorCardItem';

import MentorCardList from './MentorCardList';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/MentorCardList',
  component: MentorCardList,

  tags: ['autodocs'],

  decorators: [
    (Story) => (
      <ThemeProvider theme={THEME}>
        <MemoryRouter initialEntries={[PAGE_URL.HOME]}>
          <MobileLayout>
            <Story />
          </MobileLayout>
        </MemoryRouter>
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof MentorCardList>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MentorCardList 컴포넌트는 멘토 정보 카드를 담은 컴포넌트입니다. 멘토 정보 카드를 세로로 보여주고 있습니다.',
      },
    },
  },
  args: {
    children: (
      <>
        <MentorCardItem />
        <MentorCardItem />
      </>
    ),
  },
};
