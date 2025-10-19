import MentoringActionPanel from './MentoringActionPanel';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'ChatRoom/MentoringActionPanel',
  component: MentoringActionPanel,
  decorators: [(Story) => <Story />],
} satisfies Meta<typeof MentoringActionPanel>;

export default meta;
type Story = StoryObj<typeof meta>;

export const MenteePanel: Story = {
  args: {
    mentorName: '김멘토',
    price: 5000,
    profileImageUrl: null,
    mentorOwned: false,
    onPaymentRequestClick: () => {},
    onReviewRequestClick: () => {},
    onEndClick: () => {},
    onPaymentClick: () => {},
    onReviewClick: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          'MentoringActionPanel 컴포넌트는 채팅방 상단에 멘토링 정보와 함께 액션 버튼을 제공하는 컴포넌트입니다. 멘티의 경우 송금하기, 리뷰하기 버튼이 나타납니다.',
      },
    },
  },
};

export const MentorPanel: Story = {
  args: {
    mentorName: '김멘토',
    price: 5000,
    profileImageUrl: null,
    mentorOwned: true,
    onPaymentRequestClick: () => {},
    onReviewRequestClick: () => {},
    onEndClick: () => {},
    onPaymentClick: () => {},
    onReviewClick: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          'MentoringActionPanel 컴포넌트는 채팅방 상단에 멘토링 정보와 함께 액션 버튼을 제공하는 컴포넌트입니다. 멘토의 경우 송금요청, 리뷰요청, 종료하기 버튼이 나타납니다 ',
      },
    },
  },
};
