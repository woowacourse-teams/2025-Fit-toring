import styled from '@emotion/styled';

import beginnerAvif from '../../../../common/assets/images/beginner.avif';
import beginnerPng from '../../../../common/assets/images/beginner.png';
import expertAvif from '../../../../common/assets/images/expert.avif';
import expertPng from '../../../../common/assets/images/expert.png';

function UserLevelGuide() {
  return (
    <S_Container>
      <S_UserWrapper>
        <picture>
          <source srcSet={beginnerAvif} type="image/avif" />
          <S_Img src={beginnerPng} alt="초보자 이미지" loading="lazy" />
        </picture>
        <S_Name>운동 초보자</S_Name>
        <S_Description>
          소액으로 1회성 <br />
          온라인 운동 멘토링
        </S_Description>
      </S_UserWrapper>
      <S_UserWrapper>
        <picture>
          <source srcSet={expertAvif} type="image/avif" />
          <S_Img src={expertPng} alt="숙련자 이미지" loading="lazy" />
        </picture>
        <S_Name>운동 숙련자</S_Name>
        <S_Description>
          전문성과 경험을 공유해 <br />
          추가 수익 창출
        </S_Description>
      </S_UserWrapper>
    </S_Container>
  );
}

export default UserLevelGuide;

const S_Container = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 3.5rem;

  padding: 7rem 3rem;

  line-height: normal;
`;

const S_UserWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
`;

const S_Img = styled.img`
  width: 65px;
  aspect-ratio: 306 / 400;
`;

const S_Name = styled.p`
  font-weight: bold;
  font-size: 1.7rem;
`;

const S_Description = styled.p`
  font-size: 1.5rem;
  text-align: center;
`;
