import styled from '@emotion/styled';

import Skeleton from '../../../../common/components/Skeleton/Skeleton';

function MentoringUpdateFormSkeleton() {
  return (
    <>
      <S_Section>
        <S_TitleSkeleton />
        <S_TitleBarWrapper>
          <S_TitleAccentBar />
          <S_TitleBaseBar />
        </S_TitleBarWrapper>
        <S_FieldSkeleton />
        <S_FieldSkeleton />
        <S_ShortFieldSkeleton />
      </S_Section>

      <S_Section>
        <S_TitleSkeleton />
        <S_TitleBarWrapper>
          <S_TitleAccentBar />
          <S_TitleBaseBar />
        </S_TitleBarWrapper>
        <S_ProfileImageSkeleton />
      </S_Section>

      <S_Section>
        <S_TitleSkeleton />
        <S_TitleBarWrapper>
          <S_TitleAccentBar />
          <S_TitleBaseBar />
        </S_TitleBarWrapper>
        <S_FieldSkeleton />
        <S_TextareaSkeleton />
      </S_Section>
    </>
  );
}

export default MentoringUpdateFormSkeleton;

const S_Section = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1.4rem;
`;

const S_TitleSkeleton = styled(Skeleton)`
  width: 12rem;
  height: 2.7rem;
`;

const S_TitleBarWrapper = styled.div`
  display: flex;
`;

const S_TitleAccentBar = styled(Skeleton)`
  width: 6rem;
  height: 0.2rem;
  border-radius: 0;
`;

const S_TitleBaseBar = styled(Skeleton)`
  flex: 1;

  height: 0.2rem;
  border-radius: 0;
`;

const S_FieldSkeleton = styled(Skeleton)`
  width: 100%;
  height: 5.4rem;
  border-radius: 12px;
`;

const S_ShortFieldSkeleton = styled(Skeleton)`
  width: 72%;
  height: 5.4rem;
  border-radius: 12px;
`;

const S_ProfileImageSkeleton = styled(Skeleton)`
  width: 100%;
  height: 18rem;
  border-radius: 16px;
`;

const S_TextareaSkeleton = styled(Skeleton)`
  width: 100%;
  height: 18rem;
  border-radius: 12px;
`;
