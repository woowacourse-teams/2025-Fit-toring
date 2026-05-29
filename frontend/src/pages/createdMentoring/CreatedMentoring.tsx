import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import downIcon from '../../common/assets/images/downIcon.svg';
import { useAuth } from '../../common/components/AuthProvider/AuthProvider';
import Button from '../../common/components/Button/Button';
import PullToRefresh from '../../common/components/PullToRefresh/PullToRefresh';
import { isPullToRefreshEnabled } from '../../common/components/PullToRefresh/utils';
import { PAGE_URL } from '../../common/constants/url';
import useMyMentoringId from '../home/hooks/useMyMentoringId';

import MentoringApplicationItem from './components/MentoringApplicationItem/MentoringApplicationItem';
import MentoringApplicationList from './components/MentoringApplicationList/MentoringApplicationList';
import useMentoringApplicationList from './hooks/useMentoringApplicationList';

function CreatedMentoring() {
  const { mentoringApplicationList, refetchMentoringApplicationList } =
    useMentoringApplicationList();

  const handleActionButtonsClick = async () => {
    await refetchMentoringApplicationList();
  };

  const { authenticated } = useAuth();
  const { myMentoringId, refetchMyMentoringId } =
    useMyMentoringId(authenticated);

  const navigate = useNavigate();

  const handleMentoringShowButtonClick = () => {
    if (!myMentoringId) {
      return;
    }

    navigate(`${PAGE_URL.DETAIL}/${myMentoringId}`);
  };

  const handleFilterClick = () => {
    alert('기능 추가 예정입니다.');
  };

  const handleRefresh = async () => {
    await Promise.all([
      refetchMentoringApplicationList(),
      refetchMyMentoringId(),
    ]);
  };

  return (
    <S_Container>
      <PullToRefresh
        enabled={isPullToRefreshEnabled()}
        onRefresh={handleRefresh}
      >
        {myMentoringId ? (
          <S_ContentsWrapper>
            <S_MentoringSectionHeader>
              <S_Title>
                예약 목록{' '}
                <S_TitleCount>({mentoringApplicationList.length})</S_TitleCount>
              </S_Title>
              <S_SmallButton onClick={handleFilterClick} type="button">
                <S_Text>전체보기</S_Text>
                <S_DownIcon src={downIcon} alt="카테고리 열기 아이콘" />
              </S_SmallButton>
            </S_MentoringSectionHeader>
            <MentoringApplicationList>
              {mentoringApplicationList.map((item) => (
                <MentoringApplicationItem
                  key={item.reservationId}
                  mentoringApplication={item}
                  onActionButtonsClick={handleActionButtonsClick}
                />
              ))}
            </MentoringApplicationList>
          </S_ContentsWrapper>
        ) : (
          <S_ContentsWrapper>
            <S_EmptyText>내가 운영하는 멘토링이 없습니다.</S_EmptyText>
          </S_ContentsWrapper>
        )}
      </PullToRefresh>
      {myMentoringId && (
        <S_ButtonWrapper>
          <S_Button
            variant="newPrimary"
            size="full"
            onClick={handleMentoringShowButtonClick}
          >
            내 멘토링 보러가기
          </S_Button>
        </S_ButtonWrapper>
      )}
    </S_Container>
  );
}

export default CreatedMentoring;

const S_Container = styled.section`
  display: flex;
  flex-direction: column;

  width: 100%;
  min-height: calc(100vh - 5.7rem);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_ContentsWrapper = styled.div`
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  gap: 1.4rem;

  width: 100%;
  padding: 1.6rem 1.4rem 2rem;
`;

const S_MentoringSectionHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_B}
`;

const S_TitleCount = styled.span`
  color: #94a3b8;
  font-weight: 600;
`;

const S_SmallButton = styled.button`
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;

  padding: 0.6rem 1.2rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 7px;

  background-color: ${({ theme }) => theme.BG.WHITE};
  cursor: pointer;
`;

const S_Text = styled.span`
  ${({ theme }) => theme.TYPOGRAPHY.C3_R};
  color: ${({ theme }) => theme.FONT.B03};
`;

const S_DownIcon = styled.img`
  width: 1rem;
  aspect-ratio: 1 / 1;
`;

const S_ButtonWrapper = styled.section`
  display: flex;
  align-items: center;
  justify-content: center;
  position: sticky;
  bottom: 0;

  width: 100%;
  padding: 1.4rem 1.8rem;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.LIGHT};

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_Button = styled(Button)`
  padding: 1.6rem;

  background-color: ${({ theme }) => theme.BG.BLACK};

  color: ${({ theme }) => theme.FONT.W01};
`;

const S_EmptyText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
`;
