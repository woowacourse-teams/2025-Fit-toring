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
    <S_Container>
      {Array.from({ length: MAX_RATING_COUNT }, (_, index) => {
        const score = index + 1;
        const isFilled = score <= rating;
        return (
          <S_StarButton
            key={score}
            type="button"
            onClick={() => onRatingChange(score)}
          >
            <S_StarIcon
              src={isFilled ? filledStar : emptyStar}
              alt={`${score}점`}
            />
          </S_StarButton>
        );
      })}
    </S_Container>
  );
}

export default StarRating;

const S_Container = styled.div`
  display: flex;
  gap: 0.5rem;
`;

const S_StarButton = styled.button`
  padding: 0;
  border: 0;

  background: transparent;
  cursor: pointer;
`;

const S_StarIcon = styled.img`
  display: block;

  width: 2rem;
  height: 2rem;
`;
