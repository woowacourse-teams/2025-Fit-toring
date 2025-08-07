import { CAREER_ERROR_MESSAGE } from '../../constants/career';
import { INTRODUCE_ERROR_MESSAGE } from '../../constants/introduce';

import IntroduceSection from './IntroduceSection';

import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'mentoringCreate/IntroduceSection',
  component: IntroduceSection,

  decorators: [(Story) => <Story />],
} satisfies Meta<typeof IntroduceSection>;

export default meta;
type Story = StoryObj<typeof meta>;

export const DefaultIntroduceSection: Story = {
  args: {
    onIntroduceChange: () => {},
    introduceErrorMessage: '',
    careerErrorMessage: '',
  },
  parameters: {
    docs: {
      description: {
        story:
          '멘토링 생성 페이지의 자기소개 섹션입니다. 멘토가 자신의 소개 및 경력을 작성할 수 있습니다.',
      },
    },
  },
};

export const ErrorIntroduceSection: Story = {
  args: {
    onIntroduceChange: () => {},
    introduceErrorMessage: INTRODUCE_ERROR_MESSAGE.INTRODUCE_MAX_LENGTH,
    careerErrorMessage: CAREER_ERROR_MESSAGE.CAREER_INVALID,
  },
  parameters: {
    docs: {
      description: {
        story: '에러 상태의 IntroduceSection 컴포넌트입니다.',
      },
    },
  },
};
