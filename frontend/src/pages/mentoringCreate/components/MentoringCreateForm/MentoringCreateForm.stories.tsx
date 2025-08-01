import MentoringCreateForm from './MentoringCreateForm';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/MentoringCreateForm',
  component: MentoringCreateForm,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof MentoringCreateForm>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultMentoringCreateForm: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MentoringCreateForm 컴포넌트는 멘토링 개설 페이지의 메인 폼을 구성합니다.',
      },
    },
  },
};
