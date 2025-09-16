import styled from '@emotion/styled';

import deleteIcon from '../../../../common/assets/images/deleteIcon.svg';
import uploadIcon from '../../../../common/assets/images/uploadIcon.svg';
import useAsyncLoadingInput from '../../../hooks/useAsyncLoadingInput';
import usePreviewImage from '../../../hooks/usePreviewImage';
import { convertHeicToJpegIfNeeded } from '../../../utils/heicFile/convertHeicToJpegIfNeeded';
import LoadingSpinner from '../../LoadingSpinner/LoadingSpinner';
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

  const handleError = (error: unknown) => {
    if (error instanceof Error) {
      console.error(error.message);
      alert('이미지 업로드에 실패했습니다. 다시 시도해주세요.');
    } else if (typeof error === 'string') {
      console.error(error);
      alert('알 수 없는 오류가 발생했습니다. 다시 시도해주세요.');
    }
  };

  const { isLoading, handleCallback: handleProfileImageInputClick } =
    useAsyncLoadingInput({
      callback: handleProfileImageInputChange,
      onError: handleError,
    });

  return (
    <section>
      <TitleSeparator>프로필 사진</TitleSeparator>
      <S_ProfileWrapper>
        <S_DeleteButton type="button" onClick={handleDeleteProfileImageClick}>
          <img src={deleteIcon} alt="삭제 아이콘" />
        </S_DeleteButton>
        <S_ProfileInputWrapper>
          {isLoading ? (
            <LoadingSpinner />
          ) : (
            <>
              <S_HiddenInput
                type="file"
                accept="image/*"
                id="profileImage"
                onChange={handleProfileImageInputClick}
              />
              {previewUrl ? (
                <S_PreviewImage src={previewUrl} alt="프로필 사진 미리보기" />
              ) : (
                <S_ContentWrapper>
                  <S_UploadIcon src={uploadIcon} alt="업로드 아이콘" />
                  {/* TODO: 드래그를 통한 업로드 기능 추가 */}
                  <S_GuideText>
                    <strong>클릭하여 업로드</strong>
                  </S_GuideText>{' '}
                  <S_FileTypeText>(최대 30MB)</S_FileTypeText>
                </S_ContentWrapper>
              )}
            </>
          )}
        </S_ProfileInputWrapper>
      </S_ProfileWrapper>
    </section>
  );
}

export default ProfileSection;

const S_ProfileWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 1.5rem;
`;

const S_ProfileInputWrapper = styled.label`
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

const S_ContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.8rem;

  width: 100%;
  height: 100%;
`;

const S_DeleteButton = styled.button`
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

const S_PreviewImage = styled.img`
  width: 100%;
  height: 100%;
  object-fit: contain;

  border-radius: 16px;
`;

const S_GuideText = styled.p`
  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.LB4_R}
  text-align: center;

  & > strong {
    color: ${({ theme }) => theme.SYSTEM.MAIN700};
    ${({ theme }) => theme.TYPOGRAPHY.LB4_B}
  }
`;

const S_FileTypeText = styled.p`
  color: ${({ theme }) => theme.FONT.G01};
  ${({ theme }) => theme.TYPOGRAPHY.B2_R}
  text-align: center;
`;
const S_HiddenInput = styled.input`
  display: none;
`;

const S_UploadIcon = styled.img`
  width: 6.4rem;
  height: 6.4rem;
`;
