import styled from '@emotion/styled';

import locationIcon from '../../../../common/assets/images/locationIcon.svg';
import startIcon from '../../../../common/assets/images/starIcon.svg';
import timeIcon from '../../../../common/assets/images/timeIcon.svg';
import CategoryTags from '../CategoryTags/CategoryTags';
import ProfileImg from '../ProfileImg/ProfileImg';
import TextWithIcon from '../TextWithIcon/TextWithIcon';

const DUMMY = {
  id: 1,
  mentor_name: '김트레이너',
  categories: ['근력 증진', '다이어트', '체형 교정'],
  price: 5000,
  image:
    'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=400&fit=crop&crop=face',
};

function MentoInfoCard() {
  return (
    <StyledContainer>
      <StyledMentoProfileWrapper>
        <ProfileImg src={DUMMY.image} />
        <StyledMetoNameText>{DUMMY.mentor_name}</StyledMetoNameText>
      </StyledMentoProfileWrapper>
      <StyledInfoWithTags>
        <StyledInfoWrapper>
          <TextWithIcon iconSrc={startIcon} text="4.5 (127)" iconName="별점" />
          <TextWithIcon
            iconSrc={locationIcon}
            text="서울 강남구"
            iconName="위치"
          />
          <TextWithIcon iconSrc={timeIcon} text="15분" iconName="시간" />
        </StyledInfoWrapper>
        <CategoryTags tagNames={DUMMY.categories} />
      </StyledInfoWithTags>
      <StyledPriceText>{DUMMY.price.toLocaleString('ko')}원</StyledPriceText>
    </StyledContainer>
  );
}

export default MentoInfoCard;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.4rem;
  width: 100%;
  padding: 2.2rem;
  background-color: white;
  border: ${({ theme }) => theme.LINE.REGULAR} 0.1rem solid;
  border-radius: 1.27rem;
`;

const StyledMentoProfileWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.4rem;
`;

const StyledMetoNameText = styled.span`
  font-size: 1.6rem;
  color: ${({ theme }) => theme.FONT.B01};
`;

const StyledInfoWithTags = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
  align-items: center;
`;

const StyledInfoWrapper = styled.div`
  display: flex;
  gap: 1.3rem;
`;

const StyledPriceText = styled.span`
  color: ${({ theme }) => theme.SYSTEM.MAIN700};
  font-size: 1.6rem;
  font-weight: bold;
`;
