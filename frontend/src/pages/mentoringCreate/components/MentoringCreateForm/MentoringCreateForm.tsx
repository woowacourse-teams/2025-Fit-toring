import { useState } from 'react';

import styled from '@emotion/styled';
import * as Sentry from '@sentry/react';

import { postMentoringCreate } from '../../apis/postMentoringCreate';
import { careerValidator } from '../../utils/careerValidator';
import { introduceValidator } from '../../utils/introduceValidator';
import { priceValidator } from '../../utils/priceValidator';
import BaseInfoSection from '../BaseInfoSection/BaseInfoSection';
import ButtonSection from '../ButtonSection/ButtonSection';
import CertificateSection from '../CertificateSection/CertificateSection';
import DetailIntroduce from '../DetailIntroduce/DetailIntroduce';
import IntroduceSection from '../IntroduceSection/IntroduceSection';
import ProfileSection from '../ProfileSection/ProfileSection';
import SpecialtySection from '../SpecialtySection/SpecialtySection';

import type { mentoringCreateFormData } from '../types/mentoringCreateFormData';

function MentoringCreateForm() {
  const [mentoringData, setMentoringData] = useState<mentoringCreateFormData>({
    price: 0,
    category: [],
    introduction: '',
    career: 0,
    content: '',
    certificateInfos: [
      {
        type: '',
        title: '',
      },
      {
        type: '',
        title: '',
      },
    ],
  });
  const [profileImageFile, setProfileImageFile] = useState<File | null>(null);
  const [certificateImageFiles, setCertificateImageFiles] = useState<File[]>(
    [],
  );

  const priceErrorMessage = priceValidator(mentoringData.price);
  const introduceErrorMessage = introduceValidator(mentoringData.introduction);
  const careerErrorMessage = careerValidator(mentoringData.career);

  const handleMentoringDataChange = (
    newData: Partial<mentoringCreateFormData>,
  ) => {
    setMentoringData((prevData) => ({
      ...prevData,
      ...newData,
    }));
  };

  const handleProfileImageChange = (file: File | null) => {
    setProfileImageFile(file);
  };

  const handleCertificateImageFilesChange = (files: File[]) => {
    setCertificateImageFiles(files);
  };

  const submitMentoringForm = async () => {
    try {
      const response = await postMentoringCreate(
        mentoringData,
        profileImageFile,
        certificateImageFiles,
      );
      if (response.status === 201) {
        alert('멘토링 등록 성공');
      }
    } catch (error) {
      console.error('멘토링 등록 실패');
      Sentry.captureException(error);
    }
  };

  const handleSubmitButtonClick = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    if (priceErrorMessage || introduceErrorMessage || careerErrorMessage) {
      alert('입력값을 확인해주세요.');
      return;
    }
    submitMentoringForm();
  };

  return (
    <StyledContainer onSubmit={handleSubmitButtonClick}>
      <BaseInfoSection
        onPriceChange={handleMentoringDataChange}
        priceErrorMessage={priceErrorMessage}
      />
      <ProfileSection onProfileImageChange={handleProfileImageChange} />
      <SpecialtySection onSpecialtyChange={handleMentoringDataChange} />
      <IntroduceSection
        onIntroduceChange={handleMentoringDataChange}
        introduceErrorMessage={introduceErrorMessage}
        careerErrorMessage={careerErrorMessage}
      />
      <CertificateSection
        onCertificateChange={handleMentoringDataChange}
        handleCertificateImageFilesChange={handleCertificateImageFilesChange}
      />
      <DetailIntroduce onDetailIntroduceChange={handleMentoringDataChange} />
      <StyledSeparator />
      <ButtonSection />
    </StyledContainer>
  );
}

export default MentoringCreateForm;

const StyledContainer = styled.form`
  display: flex;
  flex-direction: column;
  gap: 3.2rem;

  width: 100%;
  height: 100%;
  padding: 3.3rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 16px;
  box-shadow: 0 4px 6px -1px rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const StyledSeparator = styled.div`
  width: 100%;
  height: 0.1rem;

  background-color: ${({ theme }) => theme.OUTLINE.REGULAR};
`;
