import { fn } from 'storybook/test';

import IOSInstallGuideModal from './IOSInstallGuideModal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Common/IOSInstallGuideModal',
  component: IOSInstallGuideModal,
  args: {
    opened: true,
    onCloseClick: fn(),
    onLaterClick: fn(),
  },
  parameters: {
    docs: {
      description: {
        story:
          'iOS Safari에서 홈 화면 설치 방법을 단계형 카드로 안내하는 모달입니다. 첨부 이미지의 톤앤매너를 기준으로 구현했습니다.',
      },
    },
  },
} satisfies Meta<typeof IOSInstallGuideModal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {};
