import styled from '@emotion/styled';

interface ProfileImgProps {
  src: string;
}

function ProfileImg({ src }: ProfileImgProps) {
  return <StyledContainer src={src} alt="프로필 이미지" />;
}

export default ProfileImg;

const StyledContainer = styled.img`
  width: 5.6rem;
  height: 5.6rem;
  border: ${({ theme }) => theme.LINE.REGULAR} 0.1rem solid;
  border-radius: 50%;
  object-fit: cover;
`;
