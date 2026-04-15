import InputWithSubmitButton from './InputWithSubmitButton';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Common/InputWithSubmitButton',
  component: InputWithSubmitButton,
  decorators: [(Story) => <Story />],
} satisfies Meta<typeof InputWithSubmitButton>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    value: '',
    placeholder: '메시지를 입력하세요',
    onChange: () => {},
    onSubmit: (event) => {
      event.preventDefault();
    },
  },
  parameters: {
    docs: {
      description: {
        story:
          'InputWithSubmitButton 컴포넌트는 입력창과 제출 버튼을 함께 제공하는 공통 입력 영역입니다.',
      },
    },
  },
};
