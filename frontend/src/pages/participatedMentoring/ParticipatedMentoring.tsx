import styled from '@emotion/styled';

import type { ParticipatedMentoringType } from './types/participatedMentoring';

const PARTICIPATED_MENTORING_LIST: ParticipatedMentoringType[] = [
  {
    mentorName: '김트레이너',
    mentorProfileImage:
      'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.crowdpic.net%2Fphoto%2F%25ED%2592%258D%25EA%25B2%25BD-%25EC%259E%2590%25EC%2597%25B0-%25EB%2593%25A4%25ED%258C%2590-%25EC%25B4%2588%25EC%259B%2590-%25EB%2582%2598%25EB%25AC%25B4-136857%3Fsrsltid%3DAfmBOopK3sXndcC9IZQXkHBG_NVOu_ZELeaPahtqFY0gMkGhB9zkxqBr&psig=AOvVaw2i3WaVGdyiSKf3VAcV-BK2&ust=1754548076306000&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCODkj4rH9Y4DFQAAAAAdAAAAABAE',
    price: '5000',
    reservedAt: '2024-01-10',
    categories: ['근력 증진', '다이어트'],
    isReviewed: true,
  },
  {
    mentorName: '박코치',
    mentorProfileImage:
      'https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.crowdpic.net%2Fphoto%2F%25ED%2592%258D%25EA%25B2%25BD-%25EC%259E%2590%25EC%2597%25B0-%25EB%2593%25A4%25ED%258C%2590-%25EC%25B4%2588%25EC%259B%2590-%25EB%2582%2598%25EB%25AC%25B4-136857%3Fsrsltid%3DAfmBOopK3sXndcC9IZQXkHBG_NVOu_ZELeaPahtqFY0gMkGhB9zkxqBr&psig=AOvVaw2i3WaVGdyiSKf3VAcV-BK2&ust=1754548076306000&source=images&cd=vfe&opi=89978449&ved=0CBUQjRxqFwoTCODkj4rH9Y4DFQAAAAAdAAAAABAE',
    price: '4000',
    reservedAt: '2024-01-12',
    categories: ['요가', '필라테스'],
    isReviewed: false,
  },
];

function ParticipatedMentoring() {
  return (
    <StyledContainer>
      <StyledTitle>참여한 멘토링</StyledTitle>
      <StyledWrapper>
        <StyledInfoWrapper>
          <StyledSubTitle>
            참여한 멘토링 목록 ({PARTICIPATED_MENTORING_LIST.length}건)
          </StyledSubTitle>
          <StyledDescription>
            내가 신청한 멘토링 목록을 확인하고 완료된 멘토링에 대해 리뷰를
            작성할 수 있습니다.
          </StyledDescription>
        </StyledInfoWrapper>
        <StyledLine />
      </StyledWrapper>
    </StyledContainer>
  );
}

export default ParticipatedMentoring;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  width: 100%;
  height: 100%;
  padding: 2rem;
`;

const StyledTitle = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
`;

const StyledWrapper = styled.div`
  display: flex;
  flex-direction: column;

  width: 100%;
  height: 100%;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;
  box-shadow: 0 0.4rem 1.6rem rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const StyledInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  padding: 2.5rem 2rem;
`;

const StyledSubTitle = styled.h3`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R}
`;

const StyledDescription = styled.p`
  word-break: keep-all;

  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B1_R}
`;

const StyledLine = styled.hr`
  width: 100%;
  height: 1px;
  margin: 0;
  border: none;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;
