import BaseInfo from './BaseInfo';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/BaseInfo',
  component: BaseInfo,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof BaseInfo>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultBaseInfo: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'BaseInfo 컴포넌트는 멘토링 개설 페이지의 기본 정보 섹션을 구성합니다. 멘토의 이름, 상담료, 전화번호를 입력할 수 있는 폼을 포함하고 있습니다.',
      },
    },
  },
};
