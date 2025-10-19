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
    <div>
      {mentorDetail ? (
        <S_Container
          role="region"
          aria-label={`${mentorDetail.mentorName} 별점${mentorDetail.ratingAverage}점 별점 참여자${mentorDetail.ratingCount}명 15분에 ${mentorDetail.price}원 카테고리${mentorDetail.categories.join(' ')}`}
          tabIndex={0}
        >
          <S_MentorProfileWrapper aria-hidden="true">
            <ProfileImg src={mentorDetail.profileImageUrl} />
            <S_MentorNameText>{mentorDetail.mentorName}</S_MentorNameText>
          </S_MentorProfileWrapper>
          <S_InfoWithTags aria-hidden="true">
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
          <S_PriceText aria-hidden="true">
            {mentorDetail.price.toLocaleString('ko')}원
          </S_PriceText>
        </S_Container>
      ) : (
        <div>로딩중</div>
      )}
    </div>
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

const S_MentorNameText = styled.span`
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
