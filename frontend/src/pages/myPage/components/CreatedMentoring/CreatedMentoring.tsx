import styled from '@emotion/styled';

function CreatedMentoring() {
  return <StyledContainer>개설한 멘토링!!!!!!</StyledContainer>;
}

export default CreatedMentoring;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;

  width: 100%;
  height: 100%;
  padding: 2rem;

  font-size: 3rem;
`;
