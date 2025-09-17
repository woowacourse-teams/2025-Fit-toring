import Profile from './ProfileSection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/Profile',
  component: Profile,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof Profile>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultProfile: Story = {
  args: {
    profileImg: 'https://example.com/profile.png',
    mentorName: '김트레이너',
    categories: ['식단관리', '체력 증진', '근력 운동'],
    ratingAverage: '3.7',
    ratingCount: 10,
    introduction:
      '5년차 전문 트레이너로 개인 맞춤 운동 및 식단 코칭을 제공합니다.',
  },
  parameters: {
    docs: {
      description: {
        story:
          'Profile 컴포넌트는 상세 정보 페이지의 프로필 섹션을 구성합니다. 트레이너의 이름, 평점, 카테고리 태그 등을 포함하고 있습니다.',
      },
    },
  },
};
