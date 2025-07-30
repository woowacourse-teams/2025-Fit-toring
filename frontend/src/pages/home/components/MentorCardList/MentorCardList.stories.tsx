import { MemoryRouter } from 'react-router-dom';

import { PAGE_URL } from '../../../../common/constants/url';
import MentorCardItem from '../MentorCardItem/MentorCardItem';

import MentorCardList from './MentorCardList';

import type { MentorInformation } from '../../types/MentorInformation';
import type { Meta, StoryObj } from '@storybook/react-webpack5';

const meta = {
  title: 'Home/MentorCardList',
  component: MentorCardList,

  decorators: [
    (Story) => (
      <MemoryRouter initialEntries={[PAGE_URL.HOME]}>
        <Story />
      </MemoryRouter>
    ),
  ],
} satisfies Meta<typeof MentorCardList>;

export default meta;
type Story = StoryObj<typeof meta>;

const MentorData: MentorInformation = {
  id: 1,
  mentorName: '홍길동',
  categories: ['프론트엔드', '백엔드'],
  price: 5000,
  career: 5,
  imageUrl: '',
  introduction: '안녕하세요',
};

export const Default: Story = {
  parameters: {
    docs: {
      description: {
        story:
          'MentorCardList 컴포넌트는 멘토 정보 카드를 담은 컴포넌트입니다. 멘토 정보 카드를 세로로 보여주고 있습니다.',
      },
    },
  },
  args: {
    children: (
      <>
        <MentorCardItem mentor={MentorData} />
        <MentorCardItem mentor={MentorData} />
      </>
    ),
  },
};
