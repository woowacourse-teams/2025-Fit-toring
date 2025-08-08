import { commonHandler } from '../../../../common/mock/common/handlers';

import SpecialtySection from './SpecialtySection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/SpecialtySection',
  component: SpecialtySection,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof SpecialtySection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultSpecialtySection: Story = {
  args: {
    onSpecialtyChange: () => {},
  },
  parameters: {
    msw: {
      handlers: [...commonHandler],
    },
    docs: {
      description: {
        story:
          '멘토링 생성 페이지의 전문 분야 선택 섹션입니다. 최대 3개의 전문 분야를 선택할 수 있습니다.',
      },
    },
  },
};
