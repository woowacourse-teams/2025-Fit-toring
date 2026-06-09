import styled from '@emotion/styled';

import Skeleton from '../../../../common/components/Skeleton/Skeleton';

function DetailSkeleton() {
  return (
    <>
      <S_Container aria-hidden="true">
        <S_ProfileImageSkeleton />
        <S_InfoWrapper>
          <S_InfoHeader>
            <S_MentorNameSkeleton />
            <S_CertificateButtonSkeleton />
          </S_InfoHeader>
          <S_InfoLineSkeleton />
          <S_ShortInfoLineSkeleton />
        </S_InfoWrapper>
        <S_TapWrapper>
          <S_TapSkeleton>
            <S_TapBarSkeleton />
          </S_TapSkeleton>
          <S_TapSkeleton>
            <S_TapBarSkeleton />
          </S_TapSkeleton>
        </S_TapWrapper>
        <S_ContentWrapper>
          <S_LongContentLineSkeleton />
          <S_ContentLineSkeleton />
          <S_ContentLineSkeleton />
          <S_ShortContentLineSkeleton />
        </S_ContentWrapper>
      </S_Container>
      <S_ApplySectionSkeleton aria-hidden="true">
        <S_PriceWrapper>
          <S_PriceLabelSkeleton />
          <S_PriceSkeleton />
        </S_PriceWrapper>
        <S_ApplyButtonSkeleton />
      </S_ApplySectionSkeleton>
    </>
  );
}

export default DetailSkeleton;

const S_Container = styled.div`
  margin-bottom: 12rem;
`;

const S_ProfileImageSkeleton = styled(Skeleton)`
  width: 100%;
  height: 43rem;
  border-radius: 0;
`;

const S_InfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 0.9rem;

  padding: 2.2rem 2.7rem;
`;

const S_InfoHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.6rem;
`;

const S_MentorNameSkeleton = styled(Skeleton)`
  width: 12rem;
  height: 2.7rem;
`;

const S_CertificateButtonSkeleton = styled(Skeleton)`
  flex-shrink: 0;

  width: 12rem;
  height: 4.3rem;
  border-radius: 0.7rem;
`;

const S_InfoLineSkeleton = styled(Skeleton)`
  width: 100%;
  height: 2.2rem;
`;

const S_ShortInfoLineSkeleton = styled(Skeleton)`
  width: 72%;
  height: 2.2rem;
`;

const S_TapWrapper = styled.div`
  display: flex;

  width: 100%;
`;

const S_TapSkeleton = styled.div`
  display: flex;
  flex: 1;
  align-items: center;
  justify-content: center;

  height: 5.9rem;
  border-bottom: 2px solid ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_TapBarSkeleton = styled(Skeleton)`
  width: 6.4rem;
  height: 1.8rem;
`;

const S_ContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;

  padding: 3rem 2.7rem 0;
`;

const S_ContentLineSkeleton = styled(Skeleton)`
  width: 100%;
  height: 1.8rem;
`;

const S_LongContentLineSkeleton = styled(S_ContentLineSkeleton)`
  width: 92%;
`;

const S_ShortContentLineSkeleton = styled(Skeleton)`
  width: 64%;
  height: 1.8rem;
`;

const S_ApplySectionSkeleton = styled.section`
  display: flex;
  align-items: center;
  gap: 1.5rem;
  position: fixed;
  bottom: 0;
  z-index: 100;

  width: inherit;
  height: 9.4rem;
  max-width: inherit;
  padding: 2.5rem 2.7rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};

  background-color: ${({ theme }) => theme.BG.WHITE};

  @media screen and (width > 480px) {
    margin-left: -1px;
  }

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
    border-top: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  }
`;

const S_PriceWrapper = styled.div`
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  gap: 0.5rem;
`;

const S_PriceLabelSkeleton = styled(Skeleton)`
  width: 7.2rem;
  height: 1.6rem;
`;

const S_PriceSkeleton = styled(Skeleton)`
  width: 9.4rem;
  height: 2.7rem;
`;

const S_ApplyButtonSkeleton = styled(Skeleton)`
  flex: 1;

  height: 5.1rem;
  border-radius: 0.7rem;
`;
