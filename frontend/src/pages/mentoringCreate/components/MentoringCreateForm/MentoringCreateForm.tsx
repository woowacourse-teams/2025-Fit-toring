import { useState } from 'react';

import styled from '@emotion/styled';

import { postMentoringCreate } from '../../apis/postMentoringCreate';
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
    price: null,
    category: [],
    introduction: '',
    career: null,
    content: '',
    certificate: [
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
  const handleMentoringDataChange = (
    newData: Partial<mentoringCreateFormData>,
  ) => {
    setMentoringData((prevData) => ({
      ...prevData,
      ...newData,
    }));
  };
  const [profileImageFile, setProfileImageFile] = useState<File | null>(null);
  const [certificateImageFiles, setCertificateImageFiles] = useState<File[]>(
    [],
  );

  const submitMentoringForm = async () => {
    const response = await postMentoringCreate(
      mentoringData,
      profileImageFile,
      certificateImageFiles,
    );
    if (response.status === 201) {
      alert('멘토링 등록 성공');
    } else {
      console.error('멘토링 등록 실패');
    }
  };

  const handleSubmitButtonClick = (event: React.FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    submitMentoringForm();
  };

  return (
    <StyledContainer onSubmit={handleSubmitButtonClick}>
      <BaseInfoSection onPriceChange={handleMentoringDataChange} />
      <ProfileSection />
      <SpecialtySection onSpecialtyChange={handleMentoringDataChange} />
      <IntroduceSection onIntroduceChange={handleMentoringDataChange} />
      <CertificateSection />
      <DetailIntroduce />
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
