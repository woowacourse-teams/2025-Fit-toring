import styled from '@emotion/styled';

import emptyStar from '../../../common/assets/images/emptyStarIcon.svg';
import filledStar from '../../../common/assets/images/starIcon.svg';
import { MAX_RATING_COUNT } from '../constants/starRating';

interface StarRatingProps {
  rating: number;
  maxRatingCount: number;
  onRatingChange: (rating: number) => void;
}

function StarRating({ rating, onRatingChange }: StarRatingProps) {
  return (
    <StyledContainer>
      {Array.from({ length: MAX_RATING_COUNT }, (_, index) => {
        const score = index + 1;
        const isFilled = score <= rating;
        return (
          <StyledStarButton
            key={score}
            type="button"
            onClick={() => onRatingChange(score)}
          >
            <StyledStarIcon
              src={isFilled ? filledStar : emptyStar}
              alt={`${score}점`}
            />
          </StyledStarButton>
        );
      })}
    </StyledContainer>
  );
}

export default StarRating;

const StyledContainer = styled.div`
  display: flex;
  gap: 0.5rem;
`;

const StyledStarButton = styled.button`
  padding: 0;
  border: 0;

  background: transparent;
  cursor: pointer;
`;

const StyledStarIcon = styled.img`
  display: block;

  width: 2rem;
  height: 2rem;
`;
