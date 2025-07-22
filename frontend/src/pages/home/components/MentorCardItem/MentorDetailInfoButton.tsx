import { useNavigate } from 'react-router-dom';

import Button from '../../../../common/components/Button/Button';
import { PAGE_URL } from '../../../../common/constants/url';

function MentorDetailInfoButton() {
  const navigate = useNavigate();

  const handleDetailInfoButtonClick = () => {
    navigate(PAGE_URL.DETAIL);
  };

  return (
    <Button
      variant="primary"
      size="full"
      type="button"
      onClick={handleDetailInfoButtonClick}
    >
      상세 정보 보기
    </Button>
  );
}

export default MentorDetailInfoButton;
