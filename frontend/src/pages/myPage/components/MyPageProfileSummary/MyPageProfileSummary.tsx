import styled from '@emotion/styled';

import chevronRightIcon from '../../../../common/assets/images/mypage-chevron-right.svg';
import defaultProfileImg from '../../../../common/assets/images/profileImg.svg';
import MyPageBadge from '../MyPageBadge/MyPageBadge';

interface MyPageProfileSummaryProps {
  profileImg?: string | null;
  name: string;
  roleLabel: string;
  onClick: () => void;
}

function MyPageProfileSummary({
  profileImg,
  name,
  roleLabel,
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
        <S_TitleRow>
          <S_Title>{name}</S_Title>
          <MyPageBadge
            label={roleLabel}
            color="#3a43d9"
            borderColor="#3a43d9"
          />
        </S_TitleRow>
      </S_TextGroup>
      <S_Icon src={chevronRightIcon} alt="" aria-hidden="true" />
    </S_Button>
  );
}

export default MyPageProfileSummary;

const S_Button = styled.button`
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1.8rem;

  width: 100%;
  padding: 3rem;
  border: none;
  border-bottom: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};

  background: transparent;
  appearance: none;
  cursor: pointer;

  text-align: left;
`;

const S_ProfileImage = styled.img`
  flex-shrink: 0;

  width: 8.6rem;
  height: 8.6rem;
  object-fit: cover;

  border-radius: 50%;
`;

const S_TextGroup = styled.div`
  display: flex;
  flex: 1;
  flex-direction: column;
  gap: 0.8rem;

  min-width: 0;
`;

const S_TitleRow = styled.span`
  display: flex;
  align-items: center;
  gap: 1rem;

  min-width: 0;
`;

const S_Title = styled.span`
  overflow: hidden;

  color: ${({ theme }) => theme.FONT.B01};
  text-overflow: ellipsis;

  white-space: nowrap;
  ${({ theme }) => theme.TYPOGRAPHY.H3_B}
`;

const S_Icon = styled.img`
  flex-shrink: 0;

  width: 2.4rem;
  height: 2.4rem;
`;
