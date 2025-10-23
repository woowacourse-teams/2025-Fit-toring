import { MemoryRouter } from 'react-router-dom';

import defaultImage from '../../../../common/assets/images/profileImg.svg';
import { PAGE_URL } from '../../../../common/constants/url';
import { MENTORING_DETAIL } from '../../../../common/mock/getUserInfoSummary/data';

import CertificatesImageModal from './CertificatesImageModal';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Detail/CertificatesImageModal',
  component: CertificatesImageModal,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.DETAIL]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof CertificatesImageModal>;

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    opened: true,
    onCloseClick: () => {},
    imageUrl: defaultImage,
    title: MENTORING_DETAIL.certificates[0].title,
  },
  parameters: {
    docs: {
      description: {
        story: `자격증 이미지 모달입니다.`,
      },
    },
  },
};
