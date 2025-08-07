import { PRICE_ERROR_MESSAGE } from '../../constants/price';

import BaseInfoSection from './BaseInfoSection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/BaseInfoSection',
  component: BaseInfoSection,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof BaseInfoSection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultBaseInfoSection: Story = {
  args: {
    onPriceChange: () => {},
    priceErrorMessage: '',
  },
  parameters: {
    docs: {
      description: {
        story:
          'BaseInfoSection 컴포넌트는 멘토링 개설 페이지의 기본 정보 섹션을 구성합니다. 멘토의 이름, 상담료, 전화번호를 입력할 수 있는 폼을 포함하고 있습니다.',
      },
    },
  },
};

export const ErrorBaseInfoSection: Story = {
  args: {
    onPriceChange: () => {},
    priceErrorMessage: PRICE_ERROR_MESSAGE.PRICE_INVALID,
  },
  parameters: {
    docs: {
      description: {
        story: '에러 상태의 BaseInfoSection 컴포넌트입니다.',
      },
    },
  },
};
