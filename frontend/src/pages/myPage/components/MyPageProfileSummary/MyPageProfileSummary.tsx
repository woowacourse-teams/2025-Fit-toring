import styled from '@emotion/styled';

import chevronRightGray from '../../../../common/assets/images/chevron-right-gray.svg';
import defaultProfileImg from '../../../../common/assets/images/profileImg.svg';

interface MyPageProfileSummaryProps {
  profileImg?: string | null;
  name: string;
  onClick: () => void;
}

function MyPageProfileSummary({
  profileImg,
  name,
  onClick,
}: MyPageProfileSummaryProps) {
  return (
    <S_Button
      aria-label={`${name}님의 회원정보 수정`}
      onClick={onClick}
      type="button"
    >
      <S_ProfileImage
        src={profileImg || defaultProfileImg}
        alt={`${name}님의 프로필 이미지`}
        onError={(e) => {
          e.currentTarget.src = defaultProfileImg;
        }}
      />
      <S_TextGroup>
        <S_Title>{name}</S_Title>
      </S_TextGroup>
      <S_Icon src={chevronRightGray} alt="" aria-hidden="true" />
    </S_Button>
  );
}

export default MyPageProfileSummary;

const S_Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.2rem;

  width: 100%;
  padding: 0 0 1.8rem;
  border: none;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background: transparent;
  appearance: none;
  cursor: pointer;

  text-align: left;
`;

const S_ProfileImage = styled.img`
  width: 7rem;
  height: 7rem;
  object-fit: cover;

  border-radius: 50%;
`;

const S_TextGroup = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 0.6rem;
`;

const S_Title = styled.span`
  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_B}
`;

const S_Icon = styled.img`
  flex-shrink: 0;

  width: 0.8rem;
  height: auto;
`;
