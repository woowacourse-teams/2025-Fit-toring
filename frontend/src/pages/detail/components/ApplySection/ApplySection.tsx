import { useEffect, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { getMineMentoring } from '../../../../common/apis/getMineMentoring';
import { useAuth } from '../../../../common/components/AuthProvider/AuthProvider';
import Button from '../../../../common/components/Button/Button';
import { PAGE_URL } from '../../../../common/constants/url';
import { THEME } from '../../../../common/styles/theme';
import { captureSentryError } from '../../../../common/utils/captureSentryError';

import type { MentoringDetail } from '../../../../common/types/MentoringDetail';

interface ApplySectionProps {
  price: number;
  mentoringId: string | undefined;
}

function ApplySection({ price, mentoringId }: ApplySectionProps) {
  const navigate = useNavigate();

  const { authenticated } = useAuth();

  const [mineMentoring, setMineMentoring] = useState<MentoringDetail | null>(
    null,
  );

  const createdByMe = mineMentoring?.id === Number(mentoringId);

  const handleMoveToBookingPage = () => {
    if (createdByMe) {
      navigate(`${PAGE_URL.MENTORING_UPDATE}/${mentoringId}`);
      return;
    }

    if (authenticated) {
      navigate(`${PAGE_URL.BOOKING}/${mentoringId}`);
    } else {
      navigate(PAGE_URL.LOGIN);
    }
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
          feature: 'detail',
          step: 'mine-mentoring-fetch',
        });
      }
    };

    fetchMentoring();
  }, []);

  return (
    <S_Container>
      <S_Wrapper>
        <p>15분 상담료</p>
        <strong>{price.toLocaleString()}원</strong>
      </S_Wrapper>
      <Button
        size="full"
        customStyle={css`
          padding: 1.6rem 0;

          background-color: ${THEME.BG.BLACK};

          font-size: 1.2rem;

          ${THEME.TYPOGRAPHY.LB4_R}
        `}
        onClick={handleMoveToBookingPage}
      >
        {createdByMe ? '수정하기' : '신청하기'}
      </Button>
    </S_Container>
  );
}

export default ApplySection;

const S_Container = styled.section`
  display: flex;
  align-items: center;
  gap: 1.5rem;
  position: fixed;
  bottom: 0;

  width: 48rem;
  height: 9.4rem;
  padding: 2.5rem 2.7rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};

  background-color: ${({ theme }) => theme.BG.WHITE};

  @media screen and (width >= 481px) {
    left: 50%;
    transform: translateX(-50%);
  }

  @media screen and (width <= 480px) {
    left: 0;

    width: 100%;
    border: none;
    border-top: 1px solid ${({ theme }) => theme.SYSTEM.GRAY300};
    transform: none;
  }
`;

const S_Wrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;

  width: fit-content;

  white-space: nowrap;

  & p {
    ${({ theme }) => theme.TYPOGRAPHY.B4_R};
    color: ${({ theme }) => theme.SYSTEM.GRAY800};
  }

  & strong {
    color: ${({ theme }) => theme.FONT.B01};
    font-weight: bold;
    font-size: 2.6rem;
  }
`;
