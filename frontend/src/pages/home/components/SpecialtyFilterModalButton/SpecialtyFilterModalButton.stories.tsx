import SpecialtyFilterModalButton from './SpecialtyFilterModalButton';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/SpecialtyFilterModalButton',
  component: SpecialtyFilterModalButton,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof SpecialtyFilterModalButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    handleOpenModal: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          'SpecialtyFilterModalButton 컴포넌트는 홈페이지의 중단에 위치하여 전문 분야 필터링을 할 수 있는 모달을 열기 위한 버튼입니다.',
      },
    },
  },
};
