import MenuDropDown from './MenuDropDown';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'MyPage/MenuDropDown',
  component: MenuDropDown,

  decorators: [
    (Story) => (
      <div
        style={{
          display: 'flex',
          justifySelf: 'flex-end',
          width: '100px',
        }}
      >
        <Story />
      </div>
    ),
  ],
} satisfies Meta<typeof MenuDropDown>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MenuDropDown 컴포넌트는 마이 페이지의 메뉴를 구성합니다. 사용자가 메뉴 버튼을 클릭하면 드롭다운 메뉴가 열리고, 각 메뉴 항목을 선택할 수 있습니다. 선택된 메뉴 항목은 강조 표시됩니다.',
      },
    },
  },
};
