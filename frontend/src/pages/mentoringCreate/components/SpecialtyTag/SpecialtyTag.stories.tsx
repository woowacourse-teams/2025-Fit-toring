import SpecialtyTag from './SpecialtyTag';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/SpecialtyTag',
  component: SpecialtyTag,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof SpecialtyTag>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultSpecialtySection: Story = {
  args: {
    title: '프론트엔드',
    disabled: false,
    checked: false,
    onChange: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          '멘토링 생성 페이지의 전문 분야 태그 컴포넌트입니다. 사용자가 전문 분야를 선택할 수 있도록 합니다.',
      },
    },
  },
};

export const SelectedSpecialtySection: Story = {
  args: {
    title: '프론트엔드',
    disabled: false,
    checked: true,
    onChange: () => {},
  },
  parameters: {
    docs: {
      description: {

        story: '선택된 전문 분야입니다.',
      },
    },
  },
};

export const DisabledSpecialtySection: Story = {
  args: {
    title: '프론트엔드',
    disabled: true,
    checked: false,
    onChange: () => {},
  },
  parameters: {
    docs: {
      description: {

        story: '비활성화된 전문 분야 태그입니다.',
      },
    },
  },
};
