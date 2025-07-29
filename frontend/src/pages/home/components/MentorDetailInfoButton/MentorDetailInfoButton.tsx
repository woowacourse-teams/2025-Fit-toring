import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import Button from '../../../../common/components/Button/Button';
import { PAGE_URL } from '../../../../common/constants/url';

function MentorDetailInfoButton({ id }: { id: number }) {
  const navigate = useNavigate();

  const handleDetailInfoButtonClick = () => {
    navigate(`${PAGE_URL.DETAIL}/${id}`);
  };

  return (
    <Button
      variant="primary"
      size="full"
      type="button"
      onClick={handleDetailInfoButtonClick}
      customStyle={css`
        font-size: 1.2rem;
      `}
    >
      상세 정보 보기
    </Button>
  );
}

export default MentorDetailInfoButton;
