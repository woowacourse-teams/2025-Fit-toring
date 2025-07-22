import styled from '@emotion/styled';

import profileImg from '../../../../common/assets/images/profileImg.svg';

import MentorDetailInfoButton from './MentorDetailInfoButton';

function MentorCardItem() {
  return (
    <StyledContainer>
      <StyledWrapper>
        <StyledProfileImg src={profileImg} alt="트레이너 이미지" />
      </StyledWrapper>
      <MentorDetailInfoButton />
    </StyledContainer>
  );
}

export default MentorCardItem;

const StyledContainer = styled.li`
  display: flex;
  flex-direction: column;

  width: 100%;
  height: 25.6rem;
  padding: 2.2rem 2.4rem;
  border: 1px solid ${({ theme }) => theme.LINE.REGULAR};

  background-color: ${({ theme }) => theme.BG.WHITE};
  border-radius: 12.75px;
  box-shadow:
    0 10px 15px -3px rgb(0 0 0 / 10%),
    0 4px 6px -4px rgb(0 0 0 / 10%);

  :hover {
    border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN300};
  }

  justify-content: space-between;
`;

const StyledWrapper = styled.div`
  display: flex;
  gap: 1rem;
`;

const StyledProfileImg = styled.img`
  width: 5.6rem;
  height: 5.6rem;
  border-radius: 50%;

  border: 1px solid ${({ theme }) => theme.SYSTEM.MAIN300};
`;
