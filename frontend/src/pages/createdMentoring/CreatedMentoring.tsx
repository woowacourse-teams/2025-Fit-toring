import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { getMineMentoring } from '../../common/apis/getMineMentoring';
import Button from '../../common/components/Button/Button';
import { PAGE_URL } from '../../common/constants/url';
import { captureSentryError } from '../../common/utils/captureSentryError';

import { getMentoringApplicationList } from './apis/getMentoringApplicationList';
import MentoringApplicationItem from './components/MentoringApplicationItem/MentoringApplicationItem';
import MentoringApplicationList from './components/MentoringApplicationList/MentoringApplicationList';

import type { MentoringApplication } from './types/mentoringApplication';
import type { MentoringDetail } from '../../common/types/MentoringDetail';
import type { StatusType } from '../../common/types/statusType';

function CreatedMentoring() {
  const [mentoringApplicationList, setMentoringApplicationList] = useState<
    MentoringApplication[]
  >([]);

  const navigate = useNavigate();

  useEffect(() => {
    const fetchMentoringApplicationList = async () => {
      try {
        const response = await getMentoringApplicationList();
        setMentoringApplicationList(response);
      } catch (error) {
        console.error(error);
        captureSentryError({
          error,
          level: 'warning',
          feature: 'createdMentoring',
          step: 'mentoring-application-fetch',
        });
      }
    };

    fetchMentoringApplicationList();
  }, []);

  const [mineMentoring, setMineMentoring] = useState<MentoringDetail | null>(
    null,
  );

  const handleMentoringShowButtonClick = () => {
    if (!mineMentoring) {
      return;
    }

    navigate(`${PAGE_URL.DETAIL}/${mineMentoring.id}`);
  };

  useEffect(() => {
    const fetchMentoring = async () => {
      try {
        const mentoring = await getMineMentoring();
        setMineMentoring(mentoring);
      } catch (error) {
        console.error(error);
        captureSentryError({
          error,
          level: 'warning',
          feature: 'createdMentoring',
          step: 'mine-mentoring-fetch',
        });
      }
    };

    fetchMentoring();
  }, []);

  const handleActionButtonsClick = ({
    reservationId,
    status,
    phoneNumber,
  }: {
    reservationId: number;
    status: StatusType;
    phoneNumber: string;
  }) => {
    setMentoringApplicationList((prevList) => {
      return prevList.map((item) => {
        if (item.reservationId !== reservationId) {
          return item;
        }
        return {
          ...item,
          status,
          phoneNumber,
        };
      });
    });
  };

  return (
    <S_Container>
      {mineMentoring ? (
        <>
          <S_ContentsWrapper>
            <S_MentoringSectionHeader>
              <S_Title>예약 목록 ({mentoringApplicationList.length})</S_Title>
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
  padding: 0 2rem;
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
