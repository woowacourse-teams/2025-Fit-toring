import { MemoryRouter } from 'react-router-dom';

import ChatRoomForbidden from './ChatRoomForbidden';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'ChatRoom/ChatRoomForbidden',
  component: ChatRoomForbidden,
  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={['/chat/1']}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof ChatRoomForbidden>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'ChatRoomForbidden은 채팅방 접근 권한이 없는 경우(403) 노출되는 안내 UI입니다. ' +
          '채팅방 정보 조회 시 권한 오류가 발생했을 때 사용자에게 명확한 상태를 전달하고, ' +
          '홈 화면으로 이동할 수 있는 액션을 제공합니다.',
      },
    },
  },
};
