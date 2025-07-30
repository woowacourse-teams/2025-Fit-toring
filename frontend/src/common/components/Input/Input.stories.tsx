import { ThemeProvider } from '@emotion/react';

import { THEME } from '../../styles/theme';

import Input from './Input';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Common/Input',
  component: Input,
  parameters: {},

  decorators: [
    (Story) => (
      <ThemeProvider theme={THEME}>
        <Story />
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof Input>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultInput: Story = {
  args: {
    placeholder: '기본 Input',
  },
  parameters: {
    docs: {
      description: {
        story:
          '기본 Input 컴포넌트입니다. 다양한 속성을 통해 스타일을 조정할 수 있습니다.',
      },
    },
  },
};

export const InputWithWrapper: Story = {
  args: {
    placeholder: 'Input with Wrapper',
  },
  decorators: [
    (Story) => (
      <div style={{ width: '30rem' }}>
        <Story />
      </div>
    ),
  ],
  parameters: {
    docs: {
      description: {
        story:
          '부모 컴포넌트의 너비에 맞춰 Input 컴포넌트가 확장됩니다. `customStyle` 속성을 사용하여 추가적인 스타일을 적용할 수 있습니다.',
      },
    },
  },
};
