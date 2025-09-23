import styled from '@emotion/styled';

import locationIcon from '../../../../common/assets/images/locationIcon.svg';
import startIcon from '../../../../common/assets/images/starIcon.svg';
import timeIcon from '../../../../common/assets/images/timeIcon.svg';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';
import ProfileImg from '../ProfileImg/ProfileImg';

import type { MentoringDetail } from '../../../../common/types/MentoringDetail';

interface MentorInfoCardProps {
  mentorDetail: MentoringDetail | null;
}

function MentorInfoCard({ mentorDetail }: MentorInfoCardProps) {
  return (
    <S_Container>
      {mentorDetail ? (
        <>
          <S_MentorProfileWrapper>
            <ProfileImg src={mentorDetail.profileImageUrl} />
            <S_MetorNameText>{mentorDetail.mentorName}</S_MetorNameText>
          </S_MentorProfileWrapper>
          <S_InfoWithTags>
            <S_InfoWrapper>
              <TextWithIcon
                iconSrc={startIcon}
                text={`${mentorDetail.ratingAverage} (${mentorDetail.ratingCount})`}
                iconName="별점"
              />
              <TextWithIcon
                iconSrc={locationIcon}
                text="서울 강남구"
                iconName="위치"
              />
              <TextWithIcon iconSrc={timeIcon} text="15분" iconName="시간" />
            </S_InfoWrapper>
            <CategoryTags tagNames={mentorDetail.categories} />
          </S_InfoWithTags>
          <S_PriceText>{mentorDetail.price.toLocaleString('ko')}원</S_PriceText>
        </>
      ) : (
        <div>로딩중</div>
      )}
    </S_Container>
  );
}

export default MentorInfoCard;

const S_Container = styled.div`
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

const S_MentorProfileWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1.4rem;
`;

const S_MetorNameText = styled.span`
  color: ${({ theme }) => theme.FONT.B01};
  font-size: 1.6rem;
`;

const S_InfoWithTags = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.7rem;
`;

const S_InfoWrapper = styled.div`
  display: flex;
  gap: 1.3rem;
`;

const S_PriceText = styled.span`
  color: ${({ theme }) => theme.SYSTEM.MAIN600};
  font-weight: bold;
  font-size: 1.6rem;
`;
