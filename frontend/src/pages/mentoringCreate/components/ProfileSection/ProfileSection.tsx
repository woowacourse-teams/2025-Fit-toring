import styled from '@emotion/styled';

import uploadIcon from '../../../../common/assets/images/uploadIcon.svg';
import usePreviewImage from '../../../../common/hooks/usePreviewImage';
import TitleSeparator from '../TitleSeparator/TitleSeparator';
function ProfileSection() {
  const { previewUrl, handleImageChange } = usePreviewImage();

  return (
    <section>
      <TitleSeparator>프로필 사진</TitleSeparator>
      <StyledProfileWrapper>
        {previewUrl ? (
          <>
            <StyledHiddenInput
              type="file"
              accept="image/*"
              id="profileImage"
              onChange={handleImageChange}
            />
            <StyledPreviewImage src={previewUrl} alt="프로필 사진 미리보기" />
          </>
        ) : (
          <>
            <StyledHiddenInput
              type="file"
              accept="image/*"
              id="profileImage"
              onChange={handleImageChange}
            />

            <StyledContentWrapper>
              <StyledUploadIcon src={uploadIcon} alt="업로드 아이콘" />
              {/* TODO: 드래그를 통한 업로드 기능 추가 */}
              <StyledGuideText>
                <strong>클릭하여 업로드</strong> 또는 파일을 드래그 하세요
              </StyledGuideText>{' '}
              <StyledFileTypeText>
                JPG,PNG 파일만 가능(최대 5MB)
              </StyledFileTypeText>
            </StyledContentWrapper>
          </>
        )}
      </StyledProfileWrapper>
    </section>
  );
}

export default ProfileSection;

const StyledProfileWrapper = styled.label`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.8rem;

  width: 100%;
  height: fit-content;
  padding: 4.3rem;
  border: 3px dashed #e2e8f0;
  border-radius: 16px;

  background: #f8fafc;
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
