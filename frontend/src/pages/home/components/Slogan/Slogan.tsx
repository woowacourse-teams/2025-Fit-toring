import styled from '@emotion/styled';

function Slogan() {
  return (
    <StyledContainer>
      <StyledWrapper></StyledWrapper>
    </StyledContainer>
  );
}

export default Slogan;

const StyledContainer = styled.section`
  display: flex;
  width: 100%;
  height: auto;
  padding: 3rem 1.4rem;
  justify-content: center;
  align-items: center;

  border-bottom: 1px solid ${({ theme }) => theme.LINE.REGULAR};

  background: linear-gradient(
    180deg,
    ${({ theme }) => theme.SYSTEM.MAIN50} 0%,
    #fff 100%
  );
`;

const StyledWrapper = styled.div`
  display: flex;
  height: auto;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 1.5rem;
`;
