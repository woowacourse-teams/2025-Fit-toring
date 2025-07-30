import { useArgs } from 'storybook/internal/preview-api';
import { fn } from 'storybook/test';

import Modal from './Modal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Common/Modal',
  component: Modal,
  parameters: {},

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof Modal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default = {
  args: {
    opened: true,
    onCloseClick: fn(),
    children: 'Default Modal',
  },

  parameters: {
    docs: {
      description: {
        story:
          '기본 모달입니다. `onCloseClick` 함수를 통해 모달을 닫을 수 있습니다. esc 키를 누르거나 모달 외부를 클릭하면 모달이 닫힙니다.',
      },
    },
  },

  render: function Render(args: Story['args']) {
    const [{ opened }, setOpened] = useArgs();
    const handleCloseModal = () => {
      setOpened({ opened: false });
    };

    return (
      <div style={{ width: '100rem', height: '50vh', position: 'relative' }}>
        <button onClick={() => setOpened({ opened: true })}>모달 열림</button>
        <Modal {...args} opened={opened} onCloseClick={handleCloseModal}>
          {args.children}
        </Modal>
      </div>
    );
  },
} satisfies Story;
