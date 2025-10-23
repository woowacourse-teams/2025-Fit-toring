import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import downIcon from '../../common/assets/images/downIcon.svg';
import Button from '../../common/components/Button/Button';
import { PAGE_URL } from '../../common/constants/url';

import MentoringApplicationItem from './components/MentoringApplicationItem/MentoringApplicationItem';
import MentoringApplicationList from './components/MentoringApplicationList/MentoringApplicationList';
import useMentoringApplicationList from './hooks/useMentoringApplicationList';
import useMineMentoring from './hooks/useMineMentoring';

import type { StatusType } from '../../common/types/statusType';

function CreatedMentoring() {
  const { mentoringApplicationList, updateMentoringApplicationListStatus } =
    useMentoringApplicationList();

  const handleActionButtonsClick = async ({
    reservationId,
    status,
  }: {
    reservationId: number;
    status: StatusType;
  }) => {
    await updateMentoringApplicationListStatus({ reservationId, status });
  };

  const { mineMentoring } = useMineMentoring();

  const navigate = useNavigate();

  const handleMentoringShowButtonClick = () => {
    if (!mineMentoring) {
      return;
    }

    navigate(`${PAGE_URL.DETAIL}/${mineMentoring.id}`);
  };

  const handleFilterClick = () => {
    alert('기능 추가 예정입니다.');
  };

  return (
    <S_Container>
      {mineMentoring ? (
        <>
          <S_ContentsWrapper>
            <S_MentoringSectionHeader>
              <S_Title>예약 목록 ({mentoringApplicationList.length})</S_Title>
              <S_SmallButton onClick={handleFilterClick} type="button">
                <S_DownIcon src={downIcon} alt="카테고리 열기 아이콘" />
                <S_Text>전체보기</S_Text>
              </S_SmallButton>
            </S_MentoringSectionHeader>
            <S_Wrapper>
              <MentoringApplicationList>
                {mentoringApplicationList.map((item) => (
                  <MentoringApplicationItem
                    key={item.reservationId}
                    mentoringApplication={item}
                    onActionButtonsClick={handleActionButtonsClick}
                  />
                ))}
              </MentoringApplicationList>
            </S_Wrapper>
          </S_ContentsWrapper>
          <S_ButtonWrapper>
            <S_Button
              variant="newPrimary"
              size="full"
              onClick={handleMentoringShowButtonClick}
            >
              내 멘토링 보러가기
            </S_Button>
          </S_ButtonWrapper>
        </>
      ) : (
        <S_ContentsWrapper>
          <S_EmptyText>개설한 멘토링이 없습니다.</S_EmptyText>
        </S_ContentsWrapper>
      )}
    </S_Container>
  );
}

export default CreatedMentoring;

const S_Container = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  width: 100%;
  min-height: calc(100vh - 5.7rem);
`;

const S_ContentsWrapper = styled.div`
  display: flex;
  flex-direction: column;
  flex-grow: 1;
  gap: 1rem;

  width: 100%;
  height: 100%;
  padding: 1.3rem 2rem 0;
`;

const S_MentoringSectionHeader = styled.div`
  display: flex;
  align-items: center;
  justify-content: space-between;
`;

const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
`;

const S_SmallButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.6rem;

  width: 8.4rem;
  height: 3.4rem;
  padding: 1rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 5px;

  background-color: ${({ theme }) => theme.BG.WHITE};
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

const S_Wrapper = styled.div`
  display: flex;
  flex-direction: column;

  width: 100%;
  height: 100%;

  background-color: ${({ theme }) => theme.BG.WHITE};
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
