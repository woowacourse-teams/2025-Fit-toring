import Introduction from './Introduction';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/Introduction',
  component: Introduction,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof Introduction>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultIntroduction: Story = {
  args: {
    content:
      '안녕하세요, 저는 홍길동입니다. 프론트엔드 개발자로 5년의 경력을 가지고 있습니다.',
  },
  parameters: {
    docs: {
      description: {
        story:
          'Introduction 컴포넌트는 상세 정보 페이지의 소개 섹션을 구성합니다. 트레이너의 전문성과 서비스에 대한 자세한 설명을 포함하고 있습니다.',
      },
    },
  },
};
