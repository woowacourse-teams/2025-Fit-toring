import { useState } from 'react';

import styled from '@emotion/styled';

import certificateUploadIcon from '../../../../common/assets/images/certificateUploadIcon.svg';
import deleteIcon from '../../../../common/assets/images/deleteIcon.svg';
import downIcon from '../../../../common/assets/images/downIcon.svg';
import usePreviewImage from '../../../hooks/usePreviewImage';
import { convertHeicToJpegIfNeeded } from '../../../utils/heicFile/convertHeicToJpegIfNeeded';
import LoadingSpinner from '../../LoadingSpinner/LoadingSpinner';

import type { CertificateItem } from '../../../types/certificateItem';

interface CertificateInputProps {
  id: string;
  onDeleteButtonClick: () => void;
  onCertificateChange: (id: string, changed: Partial<CertificateItem>) => void;
  onCertificateImageFileChange: (file: File) => void;
  certificateInfo: CertificateItem;
}

function CertificateInput({
  id,
  onDeleteButtonClick,
  onCertificateChange,
  onCertificateImageFileChange,
  certificateInfo,
}: CertificateInputProps) {
  const { previewUrl, handleImageChange } = usePreviewImage(
    certificateInfo.imageUrl,
  );

  const handleCertificateIdChange = (
    e: React.ChangeEvent<HTMLSelectElement>,
  ) => {
    onCertificateChange(id, { type: e.target.value });
  };

  const handleCertificateTitleChange = (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    onCertificateChange(id, { title: e.target.value });
  };

  const disabled = certificateInfo.imageUrl !== undefined;

  const handleCertificateImageInputChange = async (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const file = e.target.files?.[0];
    if (file) {
      const convertedFile = await convertHeicToJpegIfNeeded(file);

      handleImageChange(convertedFile);
      onCertificateImageFileChange(convertedFile);
    }
  };

  const [isLoading, setIsLoading] = useState(false);

  const asyncImageLoader =
    <T extends React.ChangeEvent<HTMLInputElement>>(
      callback: (e: T) => Promise<void>,
    ) =>
    async (e: T) => {
      setIsLoading(true);
      try {
        await callback(e);
      } catch (error) {
        console.error(error);
        alert('이미지 업로드에 실패했습니다. 다시 시도해주세요.');
      } finally {
        setIsLoading(false);
      }
    };

  const handleCertificateImageInputClick = asyncImageLoader(
    handleCertificateImageInputChange,
  );

  return (
    <StyledContainer>
      <StyledCertificateHeader>
        <p>자격증</p>
        <button type="button" onClick={onDeleteButtonClick}>
          <img src={deleteIcon} alt="삭제 아이콘" />
        </button>
      </StyledCertificateHeader>
      <StyledCertificateInfoText>
        자격증 정보는 인증 절차가 필요한 항목으로, 수정은 불가하고 삭제만
        가능합니다.
      </StyledCertificateInfoText>
      <StyledContentWrapper>
        <p>유형</p>
        <StyledSelect
          value={certificateInfo.type ?? 'LICENSE'}
          name="certificateType"
          onChange={handleCertificateIdChange}
          disabled={disabled}
        >
          <option value="LICENSE">자격증</option>
          <option value="EDUCATION">학력</option>
          <option value="AWARD">수상 경력</option>
          <option value="ETC">기타</option>
        </StyledSelect>
      </StyledContentWrapper>
      <StyledContentWrapper>
        <p>이름 *</p>
        <StyledNameInput
          type="text"
          placeholder="생활체육지도자 자격증 1급"
          onChange={handleCertificateTitleChange}
          required
          value={certificateInfo.title ?? ''}
          disabled={disabled}
        />
      </StyledContentWrapper>

      <StyledImageInputLabel disabled={disabled}>
        {isLoading ? (
          <LoadingSpinner />
        ) : (
          <>
            <StyledHiddenInput
              type="file"
              accept="image/*"
              id={id}
              name="certificateImage"
              onChange={handleCertificateImageInputClick}
              required={!previewUrl}
              disabled={disabled}
            />

            {previewUrl ? (
              <StyledPreviewImage src={previewUrl} alt="자격증 사진 미리보기" />
            ) : (
              <StyledUploadDescription>
                <img src={certificateUploadIcon} alt="업로드 아이콘" />
                <p>증명서/사진 업로드 [필수]</p>
                <p>(최대 30MB)</p>
              </StyledUploadDescription>
            )}
          </>
        )}
      </StyledImageInputLabel>
    </StyledContainer>
  );
}

export default CertificateInput;

const StyledContainer = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2.4rem;

  width: 100%;
  margin-bottom: 2rem;
  padding: 2.5rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 12px;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const StyledCertificateHeader = styled.div`
  display: flex;
  justify-content: space-between;

  width: 100%;

  & > p {
    ${({ theme }) => theme.TYPOGRAPHY.LB4_R};
    color: ${({ theme }) => theme.FONT.B01};
  }

  & > button {
    display: flex;
    align-items: center;
    justify-content: center;

    padding: 0;
    border: none;

    background: none;

    cursor: pointer;
  }

  & > button > img {
    width: 1.6rem;
    height: 1.6rem;
  }
`;

const StyledCertificateInfoText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  color: ${({ theme }) => theme.FONT.B01};
`;

const StyledContentWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 0.8rem;

  width: 100%;

  & > p {
    ${({ theme }) => theme.TYPOGRAPHY.B2_R};
    color: ${({ theme }) => theme.FONT.B02};
  }

  & > input {
    width: 100%;
    padding: 1.6rem;
    border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
    border-radius: 12px;

    background-color: ${({ theme }) => theme.BG.WHITE};

    ${({ theme }) => theme.TYPOGRAPHY.B3_R};
    color: ${({ theme }) => theme.FONT.B01};

    &:hover {
      border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
    }

    &:focus {
      outline: none;
      box-shadow: 0 0 0 1px ${({ theme }) => theme.SYSTEM.MAIN500};
      border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
    }

    ::placeholder {
      color: ${({ theme }) => theme.SYSTEM.GRAY200};
    }
  }
`;

const StyledSelect = styled.select`
  appearance: none;

  width: 100%;
  padding: 1.6rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 12px;

  background-color: ${({ theme }) => theme.BG.WHITE};

  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
  color: ${({ theme }) => theme.FONT.B01};
  background-image: url(${downIcon});
  background-repeat: no-repeat;
  background-position: right 10px center;
  background-size: 1.2rem;

  & > option {
    ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  }

  &:hover {
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  &:focus {
    outline: none;
    box-shadow: 0 0 0 1px ${({ theme }) => theme.SYSTEM.MAIN500};
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  &:disabled {
    opacity: 1;

    background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
  }

  &:disabled:hover {
    border-color: ${({ theme }) => theme.OUTLINE.REGULAR};
    cursor: not-allowed;
  }
`;

const StyledNameInput = styled.input<{ disabled: boolean }>`
  width: 100%;
  padding: 1.6rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 12px;

  background-color: ${({ theme }) => theme.BG.WHITE};

  ${({ theme }) => theme.TYPOGRAPHY.B3_R};
  color: ${({ theme }) => theme.FONT.B01};

  &:hover {
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  &:focus {
    outline: none;
    box-shadow: 0 0 0 1px ${({ theme }) => theme.SYSTEM.MAIN500};
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  ::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  &:disabled {
    background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
  }

  &:disabled:hover {
    border-color: ${({ theme }) => theme.OUTLINE.REGULAR};
    cursor: not-allowed;
  }
`;

const StyledImageInputLabel = styled.label<{ disabled: boolean }>`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 24rem;
  padding: 4.3rem;
  border: 2px dashed ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;

  background: ${({ theme }) => theme.BG.LIGHT};
  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
`;

const StyledHiddenInput = styled.input<{ disabled: boolean }>`
  opacity: 0;

  width: 0;
  height: 0;
  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
`;

const StyledUploadDescription = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.8rem;

  width: 100%;
  height: 100%;

  & > img {
    width: 3rem;
    height: 3rem;
  }

  & > p {
    color: ${({ theme }) => theme.FONT.G01};
    text-align: center;
    ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  }
`;

const StyledPreviewImage = styled.img`
  width: 15rem;
  height: 15rem;
  object-fit: contain;

  border-radius: 16px;
`;
