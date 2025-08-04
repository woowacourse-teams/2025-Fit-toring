import CertificateSection from './CertificateSection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/CertificateSection',
  component: CertificateSection,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof CertificateSection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultCertificateSection: Story = {
  parameters: {
    docs: {
      description: {
        story:
          '멘토링 생성 페이지의 자격증 섹션입니다. 각 자격증에 대한 사진 업로드와 삭제 기능이 포함되어 있습니다.',
      },
    },
  },
};
