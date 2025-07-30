import styled from '@emotion/styled';

import locationIcon from '../../../../common/assets/images/locationIcon.svg';
import startIcon from '../../../../common/assets/images/starIcon.svg';
import timeIcon from '../../../../common/assets/images/timeIcon.svg';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';
import ProfileImg from '../ProfileImg/ProfileImg';

import type { MentoringDetail } from '../../apis/getMentoringDetail';

interface MentoInfoCardProps {
  mentorDetail: MentoringDetail | null;
}

function MentoInfoCard({ mentorDetail }: MentoInfoCardProps) {
  return (
    <StyledContainer>
      {mentorDetail ? (
        <>
          <StyledMentoProfileWrapper>
            <ProfileImg src={mentorDetail.imageUrl} />
            <StyledMetoNameText>{mentorDetail.mentorName}</StyledMetoNameText>
          </StyledMentoProfileWrapper>
          <StyledInfoWithTags>
            <StyledInfoWrapper>
              <TextWithIcon
                iconSrc={startIcon}
                text="4.5 (127)"
                iconName="별점"
              />
              <TextWithIcon
                iconSrc={locationIcon}
                text="서울 강남구"
                iconName="위치"
              />
              <TextWithIcon iconSrc={timeIcon} text="15분" iconName="시간" />
            </StyledInfoWrapper>
            <CategoryTags tagNames={mentorDetail.categories} />
          </StyledInfoWithTags>
          <StyledPriceText>
            {mentorDetail.price.toLocaleString('ko')}원
          </StyledPriceText>
        </>
      ) : (
        <div>로딩중</div>
      )}
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
  height: 21.6rem;
  padding: 2.2rem;
  border: ${({ theme }) => theme.OUTLINE.REGULAR} 0.1rem solid;
  border-radius: 1.27rem;

  background-color: white;
`;

const StyledMentoProfileWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.4rem;
`;

const StyledMetoNameText = styled.span`
  color: ${({ theme }) => theme.FONT.B01};
  font-size: 1.6rem;
`;

const StyledInfoWithTags = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.7rem;
`;

const StyledInfoWrapper = styled.div`
  display: flex;
  gap: 1.3rem;
`;

const StyledPriceText = styled.span`
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  font-weight: bold;
  font-size: 1.6rem;
`;
