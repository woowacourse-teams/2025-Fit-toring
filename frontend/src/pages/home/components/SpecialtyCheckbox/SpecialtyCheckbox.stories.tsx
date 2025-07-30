import SpecialtyCheckbox from './SpecialtyCheckbox';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/SpecialtyCheckbox',
  component: SpecialtyCheckbox,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof SpecialtyCheckbox>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    specialty: '프론트엔드',
    checked: false,
    disabled: false,
    onChange: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          'SpecialtyCheckbox 컴포넌트는 전문 분야 선택을 위한 체크박스입니다. 사용자가 전문 분야를 선택할 수 있도록 도와줍니다.',
      },
    },
  },
};

export const Checked: Story = {
  args: {
    specialty: '백엔드',
    checked: true,
    disabled: false,
    onChange: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          'Checked 상태의 SpecialtyCheckbox 컴포넌트입니다. 사용자가 전문 분야를 선택했음을 나타냅니다.',
      },
    },
  },
};

export const Disabled: Story = {
  args: {
    specialty: '백엔드',
    checked: false,
    disabled: true,
    onChange: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          'Disabled 상태의 SpecialtyCheckbox 컴포넌트입니다. 사용자가 전문 분야를 선택할 수 없음을 나타냅니다.',
      },
    },
  },
};
