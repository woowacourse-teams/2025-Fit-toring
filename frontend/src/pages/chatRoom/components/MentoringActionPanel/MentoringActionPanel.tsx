import { css } from '@emotion/react';
import styled from '@emotion/styled';

import profileImg from '../../../../common/assets/images/profileImg.svg';
import Button from '../../../../common/components/Button/Button';
import { THEME } from '../../../../common/styles/theme';

interface MentoringActionPanelProps {
  mentorName: string;
  price: number;
  profileImageUrl: string | null;
  mentorOwned: boolean;
  onPaymentRequestClick: (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>,
  ) => void;
  onReiviewRequestClick: (
    e: React.MouseEvent<HTMLButtonElement, MouseEvent>,
  ) => void;
  onEndClick: (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
  onPaymentClick: (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
  onReviewClick: (e: React.MouseEvent<HTMLButtonElement, MouseEvent>) => void;
}

function MentoringActionPanel({
  mentorName,
  price,
  profileImageUrl,
  mentorOwned,
  onPaymentRequestClick,
  onReiviewRequestClick,
  onEndClick,
  onPaymentClick,
  onReviewClick,
}: MentoringActionPanelProps) {
  return (
    <S_Container>
      <S_MentorInfoWrapper>
        <S_MentorImgWrapper>
          <S_MentorImg
            src={profileImageUrl || profileImg}
            alt="멘토 프로필 사진"
            onError={(e) => {
              e.currentTarget.src = profileImg;
            }}
          />
        </S_MentorImgWrapper>

        <S_TextInfoWrapper>
          <S_MentorName>{mentorName}</S_MentorName>
          <S_MentorPrice>{price.toLocaleString('ko')}원</S_MentorPrice>
        </S_TextInfoWrapper>
      </S_MentorInfoWrapper>
      <S_ButtonWrapper>
        {mentorOwned ? (
          <>
            <Button
              variant="newPrimary"
              customStyle={customStyle}
              onClick={onPaymentRequestClick}
            >
              송금 요청
            </Button>
            <Button
              variant="newPrimary"
              customStyle={customStyle}
              onClick={onReiviewRequestClick}
            >
              리뷰 요청
            </Button>
            <Button
              variant="newPrimary"
              customStyle={customStyle}
              onClick={onEndClick}
            >
              종료하기
            </Button>
          </>
        ) : (
          <>
            <Button
              variant="newPrimary"
              customStyle={customStyle}
              onClick={onPaymentClick}
            >
              송금하기
            </Button>
            <Button
              variant="newPrimary"
              customStyle={customStyle}
              onClick={onReviewClick}
            >
              리뷰하기
            </Button>
          </>
        )}
      </S_ButtonWrapper>
    </S_Container>
  );
}

export default MentoringActionPanel;

const customStyle = css`
  padding: 0.7rem 1.3rem;

  color: ${THEME.FONT.B01};

  ${THEME.TYPOGRAPHY.B3_B};
`;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;

  padding: 1.2rem;
  border-bottom: 1px solid ${({ theme }) => theme.SYSTEM.GRAY50};
`;

const S_MentorInfoWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 1rem;
`;

const S_MentorImgWrapper = styled.div`
  width: 4rem;
  height: 4rem;
`;

const S_MentorImg = styled.img`
  width: 100%;
  height: 100%;
  border-radius: 10px;
  object-fit: cover;
`;

const S_TextInfoWrapper = styled.div`
  display: flex;
  flex-direction: column;
  justify-content: center;
  gap: 0.2rem;
`;

const S_MentorName = styled.div`
  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
`;

const S_MentorPrice = styled.div`
  ${({ theme }) => theme.TYPOGRAPHY.B3_B};
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  gap: 1rem;
`;
