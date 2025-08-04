import ProfileSection from './ProfileSection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/ProfileSection',
  component: ProfileSection,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof ProfileSection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultProfileSection: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'ProfileSection 컴포넌트는 멘토링 개설 페이지의 프로필 사진 업로드 영역을 구성합니다.',
      },
    },
  },
};
