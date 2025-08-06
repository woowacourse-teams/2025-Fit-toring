import styled from '@emotion/styled';

import certificateUploadIcon from '../../../../common/assets/images/certificateUploadIcon.svg';
import deleteIcon from '../../../../common/assets/images/deleteIcon.svg';
import downIcon from '../../../../common/assets/images/downIcon.svg';
import usePreviewImage from '../../../../common/hooks/usePreviewImage';

import type { CertificateItem } from '../types/certificateItem';

interface CertificateInputProps {
  id: string;
  onDeleteButtonClick: () => void;
  onCertificateChange: (id: string, changed: Partial<CertificateItem>) => void;
  onCertificateImageFileChange: (file: File) => void;
}

function CertificateInput({
  id,
  onDeleteButtonClick,
  onCertificateChange,
  onCertificateImageFileChange,
}: CertificateInputProps) {
  const { previewUrl, handleImageChange } = usePreviewImage();

  const handleCertificateIdChange = (
    event: React.ChangeEvent<HTMLSelectElement>,
  ) => {
    onCertificateChange(id, { type: event.target.value });
  };

  const handleCertificateTitleChange = (
    event: React.ChangeEvent<HTMLInputElement>,
  ) => {
    onCertificateChange(id, { title: event.target.value });
  };

  return (
    <StyledContainer>
      <StyledCertificateHeader>
        <p>자격증</p>
        <button type="button" onClick={onDeleteButtonClick}>
          <img src={deleteIcon} alt="삭제 아이콘" />
        </button>
      </StyledCertificateHeader>
      <StyledContentWrapper>
        <p>유형</p>
        <StyledSelect
          defaultValue="자격증"
          name="certificateType"
          onChange={handleCertificateIdChange}
        >
          <option value="자격증">자격증</option>
          <option value="학력">학력</option>
          <option value="수상 경력">수상 경력</option>
          <option value="기타">기타</option>
        </StyledSelect>
      </StyledContentWrapper>
      <StyledContentWrapper>
        <p>이름</p>
        <input
          type="text"
          placeholder="생활체육지도자 자격증 1급"
          onChange={handleCertificateTitleChange}
        />
      </StyledContentWrapper>

      <StyledImageInputLabel htmlFor={id}>
        <StyledHiddenInput
          type="file"
          accept="image/*"
          id={id}
          name="certificateImage"
          onChange={(event) => {
            handleImageChange(event);
            const file = event.target.files?.[0];
            if (file) {
              onCertificateImageFileChange(file);
            }
          }}
          required
        />
        {previewUrl ? (
          <StyledPreviewImage src={previewUrl} alt="자격증 사진 미리보기" />
        ) : (
          <StyledUploadDescription>
            <img src={certificateUploadIcon} alt="업로드 아이콘" />
            <p>증명서/사진 업로드 [필수]</p>
          </StyledUploadDescription>
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

  &:hover {
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }

  &:focus {
    outline: none;
    box-shadow: 0 0 0 1px ${({ theme }) => theme.SYSTEM.MAIN500};
    border-color: ${({ theme }) => theme.SYSTEM.MAIN500};
  }
`;

const StyledImageInputLabel = styled.label`
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1.8rem;

  width: 100%;
  height: fit-content;
  padding: 4.3rem;
  border: 2px dashed ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;

  background: ${({ theme }) => theme.BG.LIGHT};
  cursor: pointer;
`;

const StyledHiddenInput = styled.input`
  display: none;
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
