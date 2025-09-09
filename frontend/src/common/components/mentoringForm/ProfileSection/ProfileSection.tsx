import styled from '@emotion/styled';

import deleteIcon from '../../../../common/assets/images/deleteIcon.svg';
import uploadIcon from '../../../../common/assets/images/uploadIcon.svg';
import usePreviewImage from '../../../hooks/usePreviewImage';
import { convertHeicToJpegIfNeeded } from '../../../utils/heicFile/convertHeicToJpegIfNeeded';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

interface ProfileSectionProps {
  onProfileImageChange: (file: File | null) => void;
  profileImageUrl?: string | null;
}

function ProfileSection({
  profileImageUrl,
  onProfileImageChange,
}: ProfileSectionProps) {
  const { previewUrl, handleImageChange, updatePreviewUrl } =
    usePreviewImage(profileImageUrl);

  const handleProfileImageInputChange = async (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const file = e.target.files?.[0];
    if (file) {
      const convertedFile = await convertHeicToJpegIfNeeded(file);

      handleImageChange(convertedFile);
      onProfileImageChange(convertedFile);
    }
  };

  const handleDeleteProfileImageClick = () => {
    updatePreviewUrl('');
    onProfileImageChange(null);
  };

  return (
    <section>
      <TitleSeparator>프로필 사진</TitleSeparator>
      <StyledProfileWrapper>
        <StyledDeleteButton
          type="button"
          onClick={handleDeleteProfileImageClick}
        >
          <img src={deleteIcon} alt="삭제 아이콘" />
        </StyledDeleteButton>
        <StyledProfileInputWrapper>
          <StyledHiddenInput
            type="file"
            accept="image/*"
            id="profileImage"
            onChange={handleProfileImageInputChange}
          />
          {previewUrl ? (
            <StyledPreviewImage src={previewUrl} alt="프로필 사진 미리보기" />
          ) : (
            <StyledContentWrapper>
              <StyledUploadIcon src={uploadIcon} alt="업로드 아이콘" />
              {/* TODO: 드래그를 통한 업로드 기능 추가 */}
              <StyledGuideText>
                <strong>클릭하여 업로드</strong>
              </StyledGuideText>{' '}
              <StyledFileTypeText>(최대 30MB)</StyledFileTypeText>
            </StyledContentWrapper>
          )}
        </StyledProfileInputWrapper>
      </StyledProfileWrapper>
    </section>
  );
}

export default ProfileSection;

const StyledProfileWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 1.5rem;
`;

const StyledProfileInputWrapper = styled.label`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.8rem;

  width: 100%;
  height: fit-content;
  padding: 4.3rem;
  border: 3px dashed ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;

  background: 3px dashed ${({ theme }) => theme.BG.LIGHT};
  cursor: pointer;
`;

const StyledContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.8rem;

  width: 100%;
  height: 100%;
`;

const StyledDeleteButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;

  padding: 0;
  border: none;

  background: none;

  cursor: pointer;

  > img {
    width: 2rem;
    height: 2rem;
  }
`;

const StyledPreviewImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: contain;

  border-radius: 16px;
`;

const StyledGuideText = styled.p`
  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R}
  text-align: center;

  & > strong {
    color: ${({ theme }) => theme.SYSTEM.MAIN700};
    ${({ theme }) => theme.TYPOGRAPHY.LB4_B}
  }
`;

const StyledFileTypeText = styled.p`
  color: ${({ theme }) => theme.FONT.G01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
  text-align: center;
`;
const StyledHiddenInput = styled.input`
  display: none;
`;

const StyledUploadIcon = styled.img`
  width: 6.4rem;
  height: 6.4rem;
`;
