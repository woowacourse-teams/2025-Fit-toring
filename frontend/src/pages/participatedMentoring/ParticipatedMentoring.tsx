import { useEffect, useState } from 'react';

import styled from '@emotion/styled';
import downIcon from '../../common/assets/images/downIcon.svg';

import { StatusTypeEnum } from '../../common/types/statusType';
import { captureSentryError } from '../../common/utils/captureSentryError';

import { getParticipatedMentoringList } from './apis/getParticipatedMentoring';
import MentoringItem from './MentoringItem/MentoringItem';
import MentoringList from './MentoringList/MentoringList';

import type { ParticipatedMentoringType } from './types/participatedMentoring';

function ParticipatedMentoring() {
  const [participatedMentoringList, setParticipatedMentoringList] = useState<
    ParticipatedMentoringType[]
  >([]);

  const handleReviewSubmitButtonClick = (reservationId: number) => {
    setParticipatedMentoringList((prevList) =>
      prevList.map((item) =>
        item.reservationId === reservationId
          ? { ...item, isReviewed: true, status: StatusTypeEnum.COMPLETE }
          : item,
      ),
    );
  };

  const handleFilterClick = () => {
    alert('기능 추가 예정입니다.');
  };

  useEffect(() => {
    const fetchParticipatedMentoringList = async () => {
      try {
        const data = await getParticipatedMentoringList();
        setParticipatedMentoringList(data);
      } catch (error) {
        console.error('참여한 멘토링 목록 불러오기 실패:', error);
        captureSentryError({
          error,
          level: 'warning',
          feature: 'participatedMentoring',
          step: 'fetch-participated-mentoring-list',
        });
      }
    };
    fetchParticipatedMentoringList();
  }, []);

  return (
    <S_Container>
      <S_TitleWrapper>
        <S_Title>신청 목록 ({participatedMentoringList.length})</S_Title>
        <S_Button onClick={handleFilterClick} type="button">
          <S_DownIcon src={downIcon} alt="카테고리 열기 아이콘" />
          <S_Text>전체보기</S_Text>
        </S_Button>
      </S_TitleWrapper>
      <S_Wrapper>
        <S_InfoWrapper>
          <S_SubTitle>
            참여한 멘토링 목록 ({participatedMentoringList.length}건)
          </S_SubTitle>
          <S_Description>
            내가 신청한 멘토링 목록을 확인하고 완료된 멘토링에 대해 리뷰를
            작성할 수 있습니다.
          </S_Description>
        </S_InfoWrapper>
        <S_Line />
        {participatedMentoringList.length > 0 ? (
          <MentoringList>
            {participatedMentoringList.map((item) => (
              <MentoringItem
                key={item.reservationId}
                mentoring={item}
                handleReviewSubmitButtonClick={handleReviewSubmitButtonClick}
              />
            ))}
          </MentoringList>
        ) : (
          <S_Description>참여한 멘토링이 없습니다.</S_Description>
        )}
      </S_Wrapper>
    </S_Container>
  );
}

export default ParticipatedMentoring;

const S_Container = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  width: 100%;
  height: 100%;
  padding: 2rem;
`;
const S_TitleWrapper = styled.div`
  display: flex;
  justify-content: space-between;

  width: 100%;
`;
const S_Title = styled.h2`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB3_R}
`;

const S_Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.6rem;

  width: 9.4rem;
  height: 3.4rem;
  padding: 1rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
  border-radius: 6.75px;

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

const S_Wrapper = styled.div`
  display: flex;
  flex-direction: column;

  width: 100%;
  height: 100%;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;
  box-shadow: 0 4px 16px rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const S_InfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  padding: 2.5rem 2rem;
`;

const S_SubTitle = styled.h3`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R}
`;

const S_Description = styled.p`
  word-break: keep-all;

  color: ${({ theme }) => theme.FONT.B04};
  ${({ theme }) => theme.TYPOGRAPHY.B1_R}
`;

const S_Line = styled.hr`
  width: 100%;
  height: 1px;
  margin: 0;
  border: none;
  border-top: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
`;
