import { fn } from 'storybook/test';

import InstallPromptModal from './InstallPromptModal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Common/InstallPromptModal',
  component: InstallPromptModal,
  args: {
    opened: true,
    onCloseClick: fn(),
    onLaterClick: fn(),
    onInstallClick: fn().mockResolvedValue(undefined),
  },
  parameters: {
    docs: {
      description: {
        story:
          'Android 등 beforeinstallprompt를 지원하는 환경에서 설치 프롬프트를 띄우는 모달입니다.',
      },
    },
  },
} satisfies Meta<typeof InstallPromptModal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {};
