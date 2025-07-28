import styled from '@emotion/styled';

import locationIcon from '../../../../common/assets/images/locationIcon.svg';
import profileImg from '../../../../common/assets/images/profileImg.svg';
import starIcon from '../../../../common/assets/images/starIcon.svg';
import timeIcon from '../../../../common/assets/images/timeIcon.svg';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';
import MentorDetailInfoButton from '../MentorDetailInfoButton/MentorDetailInfoButton';

import type { MentorInformation } from '../../types/MentorInformation';

interface MentorCardItemProps {
  mentor: MentorInformation;
}

function MentorCardItem({
  mentor: { id, mentorName, categories, price, career, imageUrl, introduction },
}: MentorCardItemProps) {
  return (
    <StyledContainer>
      <StyledWrapper>
        <StyledProfileImg
          src={imageUrl || profileImg}
          alt="트레이너 이미지"
          onError={(e) => {
            e.currentTarget.src = profileImg;
          }}
        />
        <StyledInfoWrapper>
          <StyledTitle>{mentorName}</StyledTitle>
          <TextWithIcon text="4.9 (127)" iconSrc={starIcon} iconName="별점" />
          <TextWithIcon text="강남구" iconSrc={locationIcon} iconName="위치" />
          <StyledPersonalHistory>경력: {career}년</StyledPersonalHistory>
          <CategoryTags tagNames={categories} />
        </StyledInfoWrapper>
      </StyledWrapper>
      <StyledSelfIntroduction>{introduction}</StyledSelfIntroduction>
      <StyledPriceWrapper>
        <TextWithIcon text="15분" iconSrc={timeIcon} iconName="시간" />
        <StyledPrice>{price.toLocaleString()}원</StyledPrice>
      </StyledPriceWrapper>
      <MentorDetailInfoButton id={id} />
    </StyledContainer>
  );
}

export default MentorCardItem;

const StyledContainer = styled.li`
  display: flex;
  flex-direction: column;
  justify-content: space-between;

  width: 100%;
  height: 25.6rem;
  padding: 2.2rem 2.4rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 12.75px;
  box-shadow:
    0 10px 15px -3px rgb(0 0 0 / 10%),
    0 4px 6px -4px rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};

  :hover {
    border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN400};
  }
`;

const StyledWrapper = styled.div`
  display: flex;
  gap: 1rem;
`;

const StyledProfileImg = styled.img`
  width: 5.6rem;
  height: 5.6rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 50%;

  :hover {
    border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN400};
  }
`;

const StyledInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
`;

const StyledTitle = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const StyledPersonalHistory = styled.p`
  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.C2_R};
`;

const StyledSelfIntroduction = styled.p`
  overflow: hidden;

  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.C2_R};
  white-space: nowrap;
  text-overflow: ellipsis;
`;

const StyledPriceWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const StyledPrice = styled.span`
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;
