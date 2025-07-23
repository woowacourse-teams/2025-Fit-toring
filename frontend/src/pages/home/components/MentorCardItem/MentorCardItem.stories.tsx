import { ThemeProvider } from '@emotion/react';
import { MemoryRouter } from 'react-router-dom';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { PAGE_URL } from '../../../../common/constants/url';
import { THEME } from '../../../../common/styles/theme';

import MentorCardItem from './MentorCardItem';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/MentorCardItem',
  component: MentorCardItem,

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
} satisfies Meta<typeof MentorCardItem>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MentorCardItem 컴포넌트는 멘토 정보 카드입니다. 멘토 정보를 간략하게 보여주고 있습니다. 상세 정보 보기 버튼 클릭 시 멘토 상세 정보 페이지로 이동합니다.',
      },
    },
  },
};
