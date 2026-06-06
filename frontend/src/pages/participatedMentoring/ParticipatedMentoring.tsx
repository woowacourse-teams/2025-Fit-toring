import styled from '@emotion/styled';

import downIcon from '../../common/assets/images/downIcon.svg';
import PullToRefresh from '../../common/components/PullToRefresh/PullToRefresh';
import { isPullToRefreshEnabled } from '../../common/components/PullToRefresh/utils';

import useParticipatedMentoringList from './hooks/useParticipatedMentoringList';
import MentoringItem from './MentoringItem/MentoringItem';
import MentoringList from './MentoringList/MentoringList';

function ParticipatedMentoring() {
  const { participatedMentoringList, refetchParticipatedMentoringList } =
    useParticipatedMentoringList();

  const handleParticipatedMentoringListRefresh = async () => {
    await refetchParticipatedMentoringList();
  };

  const handleFilterClick = () => {
    alert('기능 추가 예정입니다.');
  };

  return (
    <S_Container>
      <S_TitleWrapper>
        <S_Title>
          신청 목록{' '}
          <S_TitleCount>({participatedMentoringList.length})</S_TitleCount>
        </S_Title>
        <S_Button onClick={handleFilterClick} type="button">
          <S_DownIcon src={downIcon} alt="카테고리 열기 아이콘" />
          <S_Text>전체보기</S_Text>
        </S_Button>
      </S_TitleWrapper>
      <PullToRefresh
        enabled={isPullToRefreshEnabled()}
        onRefresh={handleParticipatedMentoringListRefresh}
      >
        {participatedMentoringList.length > 0 ? (
          <MentoringList>
            {participatedMentoringList.map((item) => (
              <MentoringItem
                key={item.reservationId}
                mentoring={item}
                handleReviewSubmitButtonClick={
                  handleParticipatedMentoringListRefresh
                }
              />
            ))}
          </MentoringList>
        ) : (
          <S_Description>참여한 멘토링이 없습니다.</S_Description>
        )}
      </PullToRefresh>
    </S_Container>
  );
}

export default ParticipatedMentoring;

const S_Container = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;

  width: 100%;
  height: 100%;
  padding: 2rem;
`;
const S_TitleWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;

  width: 100%;
`;
const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_B}
`;

const S_TitleCount = styled.span`
  color: #94a3b8;
  font-weight: 600;
`;

const S_Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.6rem;

  width: 8.4rem;
  height: 3.4rem;
  padding: 1rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 5px;

  background: ${({ theme }) => theme.BG.WHITE};
  cursor: pointer;
`;

const S_Text = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.C4_R};
  color: ${({ theme }) => theme.SYSTEM.GRAY600};
`;

const S_DownIcon = styled.img`
  width: 1.4rem;
  aspect-ratio: 1 / 1;
`;
const S_Description = styled.p`
  word-break: keep-all;

  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;
