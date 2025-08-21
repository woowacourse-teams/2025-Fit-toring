import { useEffect, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { getMineMentoring } from '../../../../common/apis/getMineMentoring';
import { useAuth } from '../../../../common/components/AuthProvider/AuthProvider';
import Button from '../../../../common/components/Button/Button';
import { PAGE_URL } from '../../../../common/constants/url';

import type { MentoringResponse } from '../../types/MentoringResponse';
import { captureSentryError } from '../../../../common/utils/captureSentryError';

interface ApplySectionProps {
  price: number;
  mentoringId: string | undefined;
}

function ApplySection({ price, mentoringId }: ApplySectionProps) {
  const navigate = useNavigate();

  const { authenticated } = useAuth();

  const [mineMentoring, setMineMentoring] = useState<MentoringResponse | null>(
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
    <StyledContainer>
      <StyledWrapper>
        <p>15분 상담료</p>
        <strong>{price.toLocaleString()}원</strong>
      </StyledWrapper>
      <Button
        size="full"
        customStyle={css`
          font-size: 1.2rem;
        `}
        onClick={handleMoveToBookingPage}
      >
        {createdByMe ? '수정하기' : '신청하기'}
      </Button>
    </StyledContainer>
  );
}

export default ApplySection;

const StyledContainer = styled.section`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 1rem;
  position: fixed;
  bottom: 0;

  width: 48rem;
  height: 9.4rem;
  padding: 2rem 2.1rem 0.8rem;
  border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN100};
  border-radius: 8px 8px 0 0;

  background-color: ${({ theme }) => theme.SYSTEM.MAIN50};

  @media screen and (width <= 480px) {
    width: 100%;
    border: none;
  }
`;

const StyledWrapper = styled.div`
  display: flex;
  justify-content: space-between;

  width: 100%;

  & p {
    ${({ theme }) => theme.TYPOGRAPHY.B4_R};
    color: ${({ theme }) => theme.FONT.B03};
  }

  & strong {
    ${({ theme }) => theme.TYPOGRAPHY.H4_B};
    color: ${({ theme }) => theme.SYSTEM.MAIN600};
  }
`;
