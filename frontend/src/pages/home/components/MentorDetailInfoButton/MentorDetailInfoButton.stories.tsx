import { ThemeProvider } from '@emotion/react';
import { MemoryRouter } from 'react-router-dom';

import MobileLayout from '../../../../common/components/MobileLayout/MobileLayout';
import { PAGE_URL } from '../../../../common/constants/url';
import { THEME } from '../../../../common/styles/theme';

import MentorDetailInfoButton from './MentorDetailInfoButton';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/MentorDetailInfoButton',
  component: MentorDetailInfoButton,

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
} satisfies Meta<typeof MentorDetailInfoButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MentorDetailInfoButton 컴포넌트는 상세 정보 보기 버튼입니다. 상세 정보 보기 버튼 클릭 시 멘토 상세 정보 페이지로 이동합니다.',
      },
    },
  },
};
