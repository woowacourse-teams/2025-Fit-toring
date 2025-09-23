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
  args: {
    certificates: [
      {
        id: '0',
        title: '',
        type: 'LICENSE',
      },
    ],
    onAddButtonClick: () => {},
    onDeleteButtonClick: () => {},
    onCertificateChangeById: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          '멘토링 생성 페이지의 자격증 섹션입니다. 각 자격증에 대한 사진 업로드와 삭제 기능이 포함되어 있습니다.',
      },
    },
  },
};

export const WithOneCertificateSection: Story = {
  args: {
    certificates: [
      {
        id: '0',
        title: '',
        type: 'LICENSE',
      },
    ],
    onAddButtonClick: () => {},
    onDeleteButtonClick: () => {},
    onCertificateChangeById: () => {},
  },
  parameters: {
    docs: {
      description: {
        story:
          '자격증 입력 폼이 이미 하나 열려 있는 상태를 보여줍니다. certificates prop에 데이터가 존재하면 해당 개수만큼의 입력 폼이 렌더링됩니다.',
      },
    },
  },
};
