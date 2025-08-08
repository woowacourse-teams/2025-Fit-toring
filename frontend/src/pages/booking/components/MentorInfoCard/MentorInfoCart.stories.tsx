import MentoInfoCard from './MentorInfoCard';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'booking/MentoInfoCard',
  component: MentoInfoCard,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof MentoInfoCard>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story: '기본 예약페이지의 멘토 정보 카드입니다.',
      },
    },
  },
  args: {
    mentorDetail: {
      id: 3,
      mentorName: '범태',
      categories: ['다이어트'],
      price: 4500,
      career: 4,
      imageUrl: '',
      introduction: '식단 중심의 다이어트',
    },
  },
};
