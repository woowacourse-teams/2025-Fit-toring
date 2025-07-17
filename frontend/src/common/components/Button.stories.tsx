import { ThemeProvider } from '@emotion/react';
import { fn } from 'storybook/test';

import { THEME } from '../styles/theme';

import Button from './Button';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Common/Button',
  component: Button,
  parameters: {},

  tags: ['autodocs'],

  args: { onClick: fn() },
  decorators: [
    (Story) => (
      <ThemeProvider theme={THEME}>
        <Story />
      </ThemeProvider>
    ),
  ],
} satisfies Meta<typeof Button>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Primary: Story = {
  args: {
    variant: 'primary',
    children: 'Primary Button',
  },
  parameters: {
    docs: {
      description: {
        story:
          '기본 선택 버튼입니다. Primary 버튼은 기본적으로 메인 색상의 배경을 가집니다. ',
      },
    },
  },
};

export const Secondary: Story = {
  args: {
    variant: 'secondary',
    children: 'Secondary Button',
  },
  parameters: {
    docs: {
      description: {
        story: 'Secondary 버튼은 기본적으로 회색 배경을 가집니다.',
      },
    },
  },
};

export const Disabled: Story = {
  args: {
    variant: 'disabled',
    children: 'Disabled Button',
  },
  parameters: {
    docs: {
      description: {
        story: 'Disabled 버튼은 클릭할 수 없습니다.',
      },
    },
  },
};

export const Full: Story = {
  args: {
    size: 'full',

    children: 'Primary Button',
  },
  parameters: {
    docs: {
      description: {
        story: 'Full 버튼은 가로폭이 100%인 버튼입니다.',
      },
    },
  },
};

export const FitContent: Story = {
  args: {
    size: 'fit',

    children: 'Primary Button',
  },
  parameters: {
    docs: {
      description: {
        story: 'Fit 버튼은 내용에 맞춰 가로폭이 조정되는 버튼입니다.',
      },
    },
  },
};
