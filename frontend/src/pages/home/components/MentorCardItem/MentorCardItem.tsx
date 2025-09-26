import { memo } from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import profileImg from '../../../../common/assets/images/profileImg.svg';
import starIcon from '../../../../common/assets/images/starIcon.svg';
import CategoryTags from '../../../../common/components/CategoryTags/CategoryTags';
import TextWithIcon from '../../../../common/components/TextWithIcon/TextWithIcon';
import { PAGE_URL } from '../../../../common/constants/url';

import type { MentorInformation } from '../../types/MentorInformation';

interface MentorCardItemProps {
  mentor: MentorInformation;
}

function MentorCardItem({
  mentor: {
    id,
    mentorName,
    categories,
    price,
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
    <S_Container onClick={handleDetailInfoButtonClick}>
      <S_ImageBox>
        <S_ProfileImg
          src={profileImageUrl || profileImg}
          alt="트레이너 이미지"
          onError={(e) => {
            e.currentTarget.src = profileImg;
          }}
        />
      </S_ImageBox>
      <S_Wrapper>
        <S_InfoWrapper>
          <S_Title>{mentorName}</S_Title>
          <TextWithIcon
            text={`${ratingAverage} (${ratingCount})`}
            iconSrc={starIcon}
            iconName="별점"
          />
          <CategoryTags tagNames={categories} />
        </S_InfoWrapper>
        <S_SelfIntroduction>{introduction}</S_SelfIntroduction>
        <S_PriceWrapper>
          <S_Time>15분 /</S_Time>
          <S_Price>{`${price.toLocaleString()}원`}</S_Price>
        </S_PriceWrapper>
      </S_Wrapper>
    </S_Container>
  );
}

export default memo(MentorCardItem);

const S_Container = styled.li`
  display: flex;

  width: 100%;
  height: 21.5rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 5px;

  background-color: ${({ theme }) => theme.BG.WHITE};

  cursor: pointer;
`;

const S_Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  width: 100%;
  height: 100%;
  padding: 1.4rem;
`;

const S_ImageBox = styled.div`
  flex-shrink: 0;
  overflow: hidden;

  width: 43%;
  height: 100%;
  border-radius: 5px 0 0 5px;
`;

const S_ProfileImg = styled.img`
  width: 100%;
  height: 100%;
  object-fit: cover;
`;

const S_InfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
`;

const S_Title = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R}
`;

const S_SelfIntroduction = styled.p`
  overflow: hidden;

  height: 100%;

  color: ${({ theme }) => theme.FONT.B03};
  ${({ theme }) => theme.TYPOGRAPHY.C2_R};

  word-break: break-all;
`;

const S_PriceWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 0.3rem;

  color: ${({ theme }) => theme.FONT.B01};
`;

const S_Time = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;

const S_Price = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
`;
