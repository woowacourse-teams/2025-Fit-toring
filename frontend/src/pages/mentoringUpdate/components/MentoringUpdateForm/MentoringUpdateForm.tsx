import { useEffect, useRef, useState } from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';

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
import { getMentoringDetail } from '../../../detail/apis/getMentoringDetail';
import { putMentoring } from '../../apis/putMentoring';
import {
  INITIAL_UPDATE_MENTORING_DATA,
  isInitialMentoringData,
} from '../../utils/isInitialMentoringData';

import type { MentoringUpdateFormData } from '../../types/mentoringUpdateForm';
import { captureSentryError } from '../../../../common/utils/captureSentryError';

function MentoringUpdateForm() {
  const [mentoringData, setMentoringData] = useState<MentoringUpdateFormData>(
    INITIAL_UPDATE_MENTORING_DATA,
  );
  const [profileImageFile, setProfileImageFile] = useState<File | null>(null);
  const [certificateImageFiles, setCertificateImageFiles] = useState<File[]>(
    [],
  );
  const initialCertificatesIdRef = useRef<string[]>([]);

  const priceErrorMessage = priceValidator(mentoringData.price);
  const introduceErrorMessage = introduceValidator(mentoringData.introduction);
  const careerErrorMessage = careerValidator(mentoringData.career);

  const handleMentoringDataChange = (
    newData: Partial<MentoringUpdateFormData>,
  ) => {
    setMentoringData((prevData) => ({
      ...prevData,
      ...newData,
    }));
  };

  const handleProfileImageChange = (file: File | null) => {
    setProfileImageFile(file);
    setMentoringData((prev) => ({ ...prev, profileImageUrl: null }));
  };

  const handleCertificateImageFilesChange = (files: File[]) => {
    setCertificateImageFiles(files);
  };

  const { mentoringId } = useParams();

  const submitMentoringForm = async () => {
    if (!mentoringId) {
      return;
    }

    const addedCertifications = mentoringData.certificateInfos.filter(
      (e) => !initialCertificatesIdRef.current.includes(e.id),
    );

    try {
      const response = await putMentoring({
        mentoringData: {
          ...mentoringData,
          certificateInfos: addedCertifications.map((addedCertification) => ({
            title: addedCertification.title,
            type: addedCertification.type,
          })),
          profileImageUrl: mentoringData.profileImageUrl,
        },
        profileImageFile,
        certificateImageFiles,
        mentoringId: mentoringId,
      });
      navigate(PAGE_URL.HOME);
      if (response.status === 200) {
        alert('멘토링 수정 성공');
      }
    } catch (error) {
      console.error('멘토링 수정 실패');

      captureSentryError({
        error,
        level: 'warning',
        feature: 'mentoring',
        step: 'mentoring-update',
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
  };

  const handleCancelButtonClick = () => {
    if (window.confirm('멘토링 등록을 취소하시겠습니까?')) {
      navigate(PAGE_URL.HOME);
    }
  };

  useEffect(() => {
    const fetchMentoring = async () => {
      if (mentoringId) {
        const { certificates, categories, ...mentoring } =
          await getMentoringDetail(mentoringId);

        const certificateInfosData = certificates.map((e) => ({
          id: e.certificateId,
          title: e.title,
          type: e.type,
          imageUrl: e.imageUrl,
        }));
        const { price, career, introduction, content, profileImageUrl } =
          mentoring;
        setMentoringData({
          price,
          career,
          introduction,
          content,
          category: categories,
          certificateInfos: certificateInfosData,
          profileImageUrl,
        });

        initialCertificatesIdRef.current = certificates.map(
          (e) => e.certificateId,
        );
      }
    };

    fetchMentoring();
  }, [mentoringId]);

  return (
    <StyledContainer onSubmit={handleSubmitButtonClick}>
      {!isInitialMentoringData(mentoringData) ? (
        <>
          <BaseInfoSection
            onPriceChange={handleMentoringDataChange}
            priceErrorMessage={priceErrorMessage}
            price={mentoringData.price}
          />
          <ProfileSection
            profileImageUrl={mentoringData.profileImageUrl}
            onProfileImageChange={handleProfileImageChange}
          />
          <SpecialtySection
            initialSelectedSpecialties={mentoringData.category}
            onSpecialtyChange={handleMentoringDataChange}
          />
          <IntroduceSection
            introduce={mentoringData.introduction}
            career={mentoringData.career}
            onIntroduceChange={handleMentoringDataChange}
            introduceErrorMessage={introduceErrorMessage}
            careerErrorMessage={careerErrorMessage}
          />
          <CertificateSection
            initialCertificates={mentoringData.certificateInfos}
            onCertificateChange={handleMentoringDataChange}
            handleCertificateImageFilesChange={
              handleCertificateImageFilesChange
            }
          />
          <DetailIntroduce
            detailIntroduce={mentoringData.content}
            onDetailIntroduceChange={handleMentoringDataChange}
          />
          <StyledSeparator />
          <ButtonSection onCancelButtonClick={handleCancelButtonClick} />
        </>
      ) : (
        <div>로딩중</div>
      )}
    </StyledContainer>
  );
}

export default MentoringUpdateForm;

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
