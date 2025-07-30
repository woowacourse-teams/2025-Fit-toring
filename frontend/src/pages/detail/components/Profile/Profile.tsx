import styled from '@emotion/styled';

import locationIcon from '../../../../common/assets/images/locationIcon.svg';
import defaultProfileImg from '../../../../common/assets/images/profileImg.svg';
import starIcon from '../../../../common/assets/images/starIcon.svg';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';

interface ProfileProps {
  profileImg?: string;
  mentorName: string;
  categories: string[];
}

function Profile({ profileImg, mentorName, categories }: ProfileProps) {
  return (
    <StyledContainer>
      <StyledProfileImg
        src={profileImg || defaultProfileImg}
        alt="멘토 프로필 이미지"
        onError={(e) => {
          e.currentTarget.src = defaultProfileImg;
        }}
      />
      <StyledInfoWrapper>
        <StyledTitle>{mentorName}</StyledTitle>
        <TextWithIcon
          text="4.9 (127개 리뷰)"
          iconSrc={starIcon}
          iconName="별점"
        />
        <TextWithIcon text="강남구" iconSrc={locationIcon} iconName="위치" />
        <CategoryTags tagNames={categories} />
      </StyledInfoWrapper>
    </StyledContainer>
  );
}

export default Profile;

const StyledContainer = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4rem;

  width: 100%;
  margin-top: 2.3rem;
  padding: 0 1rem;
`;

const StyledProfileImg = styled.img`
  width: 14rem;
  height: 14rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN400};
  border-radius: 50%;
`;

const StyledInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
`;

const StyledTitle = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
  color: ${({ theme }) => theme.FONT.B01}
`;
