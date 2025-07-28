import SpecialtyFilterModal from './SpecialtyFilterModal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/SpecialtyFilterModal',
  component: SpecialtyFilterModal,

  tags: ['autodocs'],

  decorators: [
    (Story) => (
      <div style={{ width: '100%', height: '50vh' }}>
        <Story />
      </div>
    ),
  ],
} satisfies Meta<typeof SpecialtyFilterModal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    opened: true,
    selectedSpecialties: [],
    handleCloseModal: () => {},
    handleApplyFinalSpecialties: () => {},
  },

  parameters: {
    docs: {
      description: {
        story:
          'SpecialtyFilterModal 컴포넌트는 전문 분야 선택을 위한 체크박스입니다. 현재 최대 3개만 선택 가능합니다. 사용자가 전문 분야를 선택할 수 있도록 도와줍니다.',
      },
    },
  },
};

export const Checked: Story = {
  args: {
    opened: true,
    selectedSpecialties: ['백엔드'],
    handleCloseModal: () => {},
    handleApplyFinalSpecialties: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          'SpecialtyFilterModal 컴포넌트는 전문 분야 선택을 위한 체크박스입니다. 사용자가 전문 분야를 선택할 수 있도록 도와줍니다.',
      },
    },
  },
};
