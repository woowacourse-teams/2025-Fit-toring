import styled from '@emotion/styled';

import profileImg from '../../../../common/assets/images/profileImg.svg';
import starIcon from '../../../../common/assets/images/starIcon.svg';
import timeIcon from '../../../../common/assets/images/timeIcon.svg';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';
import MentorDetailInfoButton from '../MentorDetailInfoButton/MentorDetailInfoButton';

import type { MentorInformation } from '../../types/MentorInformation';
import { useNavigate } from 'react-router-dom';
import { PAGE_URL } from '../../../../common/constants/url';

interface MentorCardItemProps {
  mentor: MentorInformation;
}

function MentorCardItem({
  mentor: {
    id,
    mentorName,
    categories,
    price,
    career,
    profileImageUrl,
    introduction,
    ratingAverage,
    ratingCount,
  },
}: MentorCardItemProps) {
  const navigate = useNavigate();

  const handleDetailInfoButtonClick = () => {
    navigate(`${PAGE_URL.DETAIL}/${id}`);
  };

  return (
    <StyledContainer onClick={handleDetailInfoButtonClick}>
      <StyledImageBox>
        <StyledProfileImg
          src={profileImageUrl || profileImg}
          alt="트레이너 이미지"
          onError={(e) => {
            e.currentTarget.src = profileImg;
          }}
        />
      </StyledImageBox>
      <StyledWrapper>
        <StyledInfoWrapper>
          <StyledTitle>{mentorName}</StyledTitle>
          <TextWithIcon
            text={`${ratingAverage} (${ratingCount})`}
            iconSrc={starIcon}
            iconName="별점"
          />
          <CategoryTags tagNames={categories} />
        </StyledInfoWrapper>
        <StyledSelfIntroduction>{introduction}</StyledSelfIntroduction>
        <StyledPriceWrapper>
          <StyledTime>15분 /</StyledTime>
          <StyledPrice>{`${price.toLocaleString()}원`}</StyledPrice>
        </StyledPriceWrapper>
      </StyledWrapper>
    </StyledContainer>
  );
}

export default MentorCardItem;

const StyledContainer = styled.li`
  display: flex;

  width: 100%;
  height: 21.5rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 5px;
  background-color: ${({ theme }) => theme.BG.WHITE};

  cursor: pointer;
`;

const StyledWrapper = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 1rem;
  padding: 2rem;
`;

const StyledImageBox = styled.div`
  width: 18rem;
  height: 100%;
  border-radius: 5px 0 0 5px;
  flex-shrink: 0;
  overflow: hidden;
`;

const StyledProfileImg = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const StyledInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
`;

const StyledTitle = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;

const StyledSelfIntroduction = styled.p`
  overflow: hidden;
  height: 100%;

  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.C2_R};
  white-space: wrap;
`;
const StyledPriceWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  color: ${({ theme }) => theme.FONT.B01};
  gap: 0.3rem;
`;

const StyledTime = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const StyledPrice = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
`;
