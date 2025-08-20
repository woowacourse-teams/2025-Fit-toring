import { useState } from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import BaseInfoSection from '../../../../common/components/mentoringForm/BaseInfoSection/BaseInfoSection';
import ButtonSection from '../../../../common/components/mentoringForm/ButtonSection/ButtonSection';
import CertificateSection from '../../../../common/components/mentoringForm/CertificateSection/CertificateSection';
import DetailIntroduce from '../../../../common/components/mentoringForm/DetailIntroduce/DetailIntroduce';
import IntroduceSection from '../../../../common/components/mentoringForm/IntroduceSection/IntroduceSection';
import ProfileSection from '../../../../common/components/mentoringForm/ProfileSection/ProfileSection';
import SpecialtySection from '../../../../common/components/mentoringForm/SpecialtySection/SpecialtySection';
import { PAGE_URL } from '../../../../common/constants/url';
import { careerValidator } from '../../../../common/utils/careerValidator';
import { introduceValidator } from '../../../../common/utils/introduceValidator';
import { priceValidator } from '../../../../common/utils/priceValidator';
import { postMentoringCreate } from '../../apis/postMentoringCreate';

import type { mentoringCreateFormData } from '../../../../common/types/mentoringCreateFormData';
import { captureSentryError } from '../../../../common/utils/captureSentryError';

function MentoringCreateForm() {
  const [mentoringData, setMentoringData] = useState<mentoringCreateFormData>({
    price: 0,
    category: [],
    introduction: '',
    career: 0,
    content: '',
    certificateInfos: [],
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
    const filteredCertificateInfos = mentoringData.certificateInfos.map(
      (certificateInfo) => ({
        type: certificateInfo.type,
        title: certificateInfo.title,
      }),
    );
    try {
      const response = await postMentoringCreate(
        { ...mentoringData, certificateInfos: filteredCertificateInfos },
        profileImageFile,
        certificateImageFiles,
      );

      if (response.status === 201) {
        alert('멘토링 등록 성공');
      }
    } catch (error) {
      console.error('멘토링 등록 실패');

      captureSentryError({
        error,
        level: 'warning',
        feature: 'mentoring',
        step: 'mentoring-create',
      });
    }
  };

  const navigate = useNavigate();

  const handleSubmitButtonClick = async (
    e: React.FormEvent<HTMLFormElement>,
  ) => {
    e.preventDefault();
    if (priceErrorMessage || introduceErrorMessage || careerErrorMessage) {
      alert('입력값을 확인해주세요.');
      return;
    }
    await submitMentoringForm();
    navigate(PAGE_URL.HOME);
  };

  const handleCancelButtonClick = () => {
    if (window.confirm('멘토링 등록을 취소하시겠습니까?')) {
      navigate(PAGE_URL.HOME);
    }
  };

  return (
    <StyledContainer onSubmit={handleSubmitButtonClick}>
      <BaseInfoSection
        onPriceChange={handleMentoringDataChange}
        priceErrorMessage={priceErrorMessage}
        price={mentoringData.price}
      />
      <ProfileSection onProfileImageChange={handleProfileImageChange} />
      <SpecialtySection onSpecialtyChange={handleMentoringDataChange} />
      <IntroduceSection
        introduce={mentoringData.introduction}
        career={mentoringData.career}
        onIntroduceChange={handleMentoringDataChange}
        introduceErrorMessage={introduceErrorMessage}
        careerErrorMessage={careerErrorMessage}
      />
      <CertificateSection
        onCertificateChange={handleMentoringDataChange}
        handleCertificateImageFilesChange={handleCertificateImageFilesChange}
      />
      <DetailIntroduce
        detailIntroduce={mentoringData.content}
        onDetailIntroduceChange={handleMentoringDataChange}
      />
      <StyledSeparator />
      <ButtonSection onCancelButtonClick={handleCancelButtonClick} />
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
