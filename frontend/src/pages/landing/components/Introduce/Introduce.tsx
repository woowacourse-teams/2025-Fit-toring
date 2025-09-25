import styled from '@emotion/styled';

import mockPng from '../../../../common/assets/images/mock_resized_500.png';
import mockAvif from '../../../../common/assets/images/mock_resized_converted.avif';

function Introduce() {
  return (
    <S_Container>
      <S_TextWrapper>
        <S_Title>
          온라인 운동 멘토링
          <br />
          중개 플랫폼
        </S_Title>
        <S_Texts>
          <S_Text>
            핏토링은 운동 숙련자들이
            <br />
            자신의 경험과 노하우를 공유합니다.
          </S_Text>
          <S_Text>
            운동 초보자들이 합리적인 비용으로
            <br />
            1회성 멘토링을 받을 수 있습니다.
          </S_Text>
          <S_Text>
            원하는 멘토에게 신청하여 카카오톡
            <br />
            오픈 채팅을 통해 상담을 받을 수 있어요
          </S_Text>
        </S_Texts>
      </S_TextWrapper>
      <S_ImgWrapper>
        <picture>
          <source srcSet={mockAvif} type="image/avif" />
          <S_Img src={mockPng} alt="목업" loading="lazy" />
        </picture>
      </S_ImgWrapper>
    </S_Container>
  );
}

export default Introduce;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 3rem;

  padding: 5rem 3rem;

  line-height: normal;
`;

const S_Title = styled.p`
  font-weight: bold;
  font-size: 2.5rem;
`;

const S_TextWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 3rem;

  text-align: center;
`;

const S_Texts = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.3rem;
`;

const S_Text = styled.p`
  font-size: 1.6rem;
`;

const S_ImgWrapper = styled.div`
  display: flex;
  justify-content: center;
`;

const S_Img = styled.img`
  float: right;

  width: 28.9rem;
  aspect-ratio: 484 / 947;
`;
