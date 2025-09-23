import styled from '@emotion/styled';

import certificateUploadIcon from '../../../../common/assets/images/certificateUploadIcon.svg';
import deleteIcon from '../../../../common/assets/images/deleteIcon.svg';
import downIcon from '../../../../common/assets/images/downIcon.svg';
import useAsyncLoadingInput from '../../../hooks/useAsyncLoadingInput';
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

  const handleError = (error: unknown) => {
    if (error instanceof Error) {
      console.error(error.message);
      alert('이미지 업로드에 실패했습니다. 다시 시도해주세요.');
    } else if (typeof error === 'string') {
      console.error(error);
      alert('알 수 없는 오류가 발생했습니다. 다시 시도해주세요.');
    }
  };

  const { isLoading, handleCallback: handleCertificateImageInputClick } =
    useAsyncLoadingInput({
      callback: handleCertificateImageInputChange,
      onError: handleError,
    });

  return (
    <S_Container>
      <S_CertificateHeader>
        <p>자격증</p>
        <button type="button" onClick={onDeleteButtonClick}>
          <img src={deleteIcon} alt="삭제 아이콘" />
        </button>
      </S_CertificateHeader>
      <S_CertificateInfoText>
        자격증 정보는 인증 절차가 필요한 항목으로, 수정은 불가하고 삭제만
        가능합니다.
      </S_CertificateInfoText>
      <S_ContentWrapper>
        <p>유형</p>
        <S_Select
          value={certificateInfo.type ?? 'LICENSE'}
          name="certificateType"
          onChange={handleCertificateIdChange}
          disabled={disabled}
        >
          <option value="LICENSE">자격증</option>
          <option value="EDUCATION">학력</option>
          <option value="AWARD">수상 경력</option>
          <option value="ETC">기타</option>
        </S_Select>
      </S_ContentWrapper>
      <S_ContentWrapper>
        <p>이름 *</p>
        <S_NameInput
          type="text"
          placeholder="생활체육지도자 자격증 1급"
          onChange={handleCertificateTitleChange}
          required
          value={certificateInfo.title ?? ''}
          disabled={disabled}
        />
      </S_ContentWrapper>

      <S_ImageInputLabel disabled={disabled}>
        {isLoading ? (
          <LoadingSpinner />
        ) : (
          <>
            <S_HiddenInput
              type="file"
              accept="image/*"
              id={id}
              name="certificateImage"
              onChange={handleCertificateImageInputClick}
              required={!previewUrl}
              disabled={disabled}
            />

            {previewUrl ? (
              <S_PreviewImage src={previewUrl} alt="자격증 사진 미리보기" />
            ) : (
              <S_UploadDescription>
                <img src={certificateUploadIcon} alt="업로드 아이콘" />
                <p>증명서/사진 업로드 [필수]</p>
                <p>(최대 30MB)</p>
              </S_UploadDescription>
            )}
          </>
        )}
      </S_ImageInputLabel>
    </S_Container>
  );
}

export default CertificateInput;

const S_Container = styled.div`
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

const S_CertificateHeader = styled.div`
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

const S_CertificateInfoText = styled.p`
  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
  color: ${({ theme }) => theme.FONT.B01};
`;

const S_ContentWrapper = styled.div`
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

const S_Select = styled.select`
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

const S_NameInput = styled.input<{ disabled: boolean }>`
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

const S_ImageInputLabel = styled.label<{ disabled: boolean }>`
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

const S_HiddenInput = styled.input<{ disabled: boolean }>`
  opacity: 0;

  width: 0;
  height: 0;
  cursor: ${({ disabled }) => (disabled ? 'not-allowed' : 'pointer')};
`;

const S_UploadDescription = styled.div`
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

const S_PreviewImage = styled.img`
  width: 15rem;
  height: 15rem;
  object-fit: contain;

  border-radius: 16px;
`;
