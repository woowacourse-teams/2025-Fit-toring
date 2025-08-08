import DetailIntroduce from './DetailIntroduce';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/DetailIntroduce',
  component: DetailIntroduce,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof DetailIntroduce>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultDetailIntroduce: Story = {
  args: {
    onDetailIntroduceChange: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          '멘토링 생성 페이지의 상세 소개 섹션입니다. 멘토의 수업 내용 및 자기소개를 입력할 수 있는 폼 입니다.',
      },
    },
  },
};
