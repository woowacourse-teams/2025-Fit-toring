import styled from '@emotion/styled';

import beginner from '../../../../common/assets/images/beginner.svg';
import expert from '../../../../common/assets/images/expert.svg';

function UserLevelGuide() {
  return (
    <S_Container>
      <S_UserWrapper>
        <S_Img src={beginner} alt="초보자 이미지" />
        <S_Name>운동 초보자</S_Name>
        <S_Description>
          소액으로 1회성 <br />
          온라인 운동 멘토링
        </S_Description>
      </S_UserWrapper>
      <S_UserWrapper>
        <S_Img src={expert} alt="숙련자 이미지" />
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
