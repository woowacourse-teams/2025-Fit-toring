import defaultProfileImg from '../../../../common/assets/images/profileImg.svg';

import ProfileImageModal from './ProfileImageModal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/ProfileImageModal',
  component: ProfileImageModal,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof ProfileImageModal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultProfile: Story = {
  args: {
    imageSrc: defaultProfileImg,
    onCloseClick: () => {},
    opened: true,
  },
  parameters: {
    docs: {
      description: {
        story: '프로필이미지 클릭시 전체 이미지를 보여주는 모달입니다.',
      },
    },
  },
};
