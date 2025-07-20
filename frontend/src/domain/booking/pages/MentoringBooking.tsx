import styled from '@emotion/styled';

import MentoInfoCard from '../components/MentoInfoCard/MentoInfoCard';

function MentoringBooking() {
  return (
    <StyledContainer>
      <MentoInfoCard />
    </StyledContainer>
  );
}

export default MentoringBooking;

const StyledContainer = styled.div`
  padding: 1.4rem;
`;
