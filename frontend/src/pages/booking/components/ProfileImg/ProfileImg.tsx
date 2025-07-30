import styled from '@emotion/styled';

import profileImg from '../../../../common/assets/images/profileImg.svg';

interface ProfileImgProps {
  src: string;
}

function ProfileImg({ src }: ProfileImgProps) {
  const handleImgError = (e: React.SyntheticEvent<HTMLImageElement, Event>) => {
    const target = e.target as HTMLImageElement;
    target.src = profileImg; // TODO: 임시 기본이미지 추후에 변경해야함
  };
  return (
    <StyledContainer
      src={src || profileImg}
      alt="프로필 이미지"
      onError={handleImgError}
    />
  );
}

export default ProfileImg;

const StyledContainer = styled.img`
  width: 5.6rem;
  height: 5.6rem;
  border: ${({ theme }) => theme.OUTLINE.REGULAR} 0.1rem solid;
  border-radius: 50%;
  object-fit: cover;
`;
