import styled from '@emotion/styled';

import locationIcon from '../../../../common/assets/images/locationIcon.svg';
import profileImg from '../../../../common/assets/images/profileImg.svg';
import starIcon from '../../../../common/assets/images/starIcon.svg';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';

function Profile() {
  return (
    <StyledContainer>
      <StyledProfileImg
        src={profileImg}
        alt="트레이너 이미지"
      ></StyledProfileImg>
      <StyledInfoWrapper>
        <StyledTitle>김트레이너</StyledTitle>
        <TextWithIcon
          text="4.9 (127개 리뷰)"
          iconSrc={starIcon}
          iconName="별점"
        />
        <TextWithIcon text="강남구" iconSrc={locationIcon} iconName="위치" />
        <CategoryTags tagNames={['근력 증진', '다이어트', '체형 교정']} />
      </StyledInfoWrapper>
    </StyledContainer>
  );
}

export default Profile;

const StyledContainer = styled.div`
  display: flex;
  margin-top: 4.3rem;
  align-items: center;
  justify-content: center;
  gap: 4rem;
`;

const StyledProfileImg = styled.img`
  width: 14rem;
  height: 14rem;
  border-radius: 50%;
  border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN300};
`;

const StyledInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.9rem;
`;

const StyledTitle = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;
