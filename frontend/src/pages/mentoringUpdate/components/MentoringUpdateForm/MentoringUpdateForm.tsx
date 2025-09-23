import { useEffect, useRef, useState } from 'react';

import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';
import { useParams } from 'react-router-dom';

import { getMentoringDetail } from '../../../../common/apis/getMentoringDetail';
import BaseInfoSection from '../../../../common/components/mentoringForm/BaseInfoSection/BaseInfoSection';
import ButtonSection from '../../../../common/components/mentoringForm/ButtonSection/ButtonSection';
import CertificateSection from '../../../../common/components/mentoringForm/CertificateSection/CertificateSection';
import DetailIntroduce from '../../../../common/components/mentoringForm/DetailIntroduce/DetailIntroduce';
import IntroduceSection from '../../../../common/components/mentoringForm/IntroduceSection/IntroduceSection';
import ProfileSection from '../../../../common/components/mentoringForm/ProfileSection/ProfileSection';
import SpecialtySection from '../../../../common/components/mentoringForm/SpecialtySection/SpecialtySection';
import { PAGE_URL } from '../../../../common/constants/url';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { careerValidator } from '../../../../common/utils/careerValidator';
import { introduceValidator } from '../../../../common/utils/introduceValidator';
import { priceValidator } from '../../../../common/utils/priceValidator';
import { validateChatUrl } from '../../../../common/utils/validateChatUrl';
import { deleteCertificate } from '../../apis/deleteCertificate';
import { putMentoring } from '../../apis/putMentoring';
import {
  INITIAL_UPDATE_MENTORING_DATA,
  isInitialMentoringData,
} from '../../utils/isInitialMentoringData';

import type { CertificateItem } from '../../../../common/types/certificateItem';
import type { MentoringUpdateFormData } from '../../types/mentoringUpdateForm';
import { validateTextarea } from '../../../../common/utils/validateDetail';

function MentoringUpdateForm() {
  const [mentoringData, setMentoringData] = useState<MentoringUpdateFormData>(
    INITIAL_UPDATE_MENTORING_DATA,
  );
  const [profileImageFile, setProfileImageFile] = useState<File | null>(null);
  const [certificateImageFiles, setCertificateImageFiles] = useState<File[]>(
    [],
  );
  const [deletedCertificateIds, setDeletedCertificateIds] = useState<string[]>(
    [],
  );
  const initialCertificatesIdRef = useRef<string[]>([]);

  const priceErrorMessage = priceValidator(mentoringData.price);
  const introduceErrorMessage = introduceValidator(mentoringData.introduction);
  const careerErrorMessage = careerValidator(mentoringData.career);
  const chatUrlErrorMessage = validateChatUrl(mentoringData.chatUrl);
  const detailErrorMessage = validateTextarea(mentoringData.content);

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
      (info) => !initialCertificatesIdRef.current.includes(info.id),
    );

    try {
      await Promise.all(
        deletedCertificateIds.map((id) => deleteCertificate(id)),
      );
    } catch (error) {
      console.error('자격증 삭제 실패', error);
    }

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
        mentoringId,
      });
      navigate(PAGE_URL.HOME);
      if (response.status === 200) {
        alert('멘토링 수정 성공');
      }
    } catch (error) {
      console.error('멘토링 수정 실패');

      captureSentryError({
        error,
        level: 'error',
        feature: 'mentoring',
        step: 'mentoring-update',
        extras: {
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
          mentoringId,
        },
      });
    }
  };

  const navigate = useNavigate();

  const handleSubmitButtonClick = async (
    e: React.FormEvent<HTMLFormElement>,
  ) => {
    e.preventDefault();
    if (
      priceErrorMessage ||
      introduceErrorMessage ||
      careerErrorMessage ||
      chatUrlErrorMessage ||
      detailErrorMessage
    ) {
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

  const [certificates, setCertificates] = useState<CertificateItem[]>([]);

  const onAddButtonClick = () => {
    setCertificates((prev) => [
      ...prev,
      {
        id: crypto.randomUUID(),
        title: null,
        type: 'LICENSE',
        file: undefined,
      },
    ]);
  };

  const onDeleteButtonClick = (id: string) => {
    const updated = certificates.filter((item) => item.id !== id);

    setCertificates(updated);

    const finalCertificates = updated.map(({ title, type, id, imageUrl }) => ({
      id,
      title,
      type,
      imageUrl,
    }));
    handleMentoringDataChange({ certificateInfos: finalCertificates });

    const files = updated
      .map((item) => item.file)
      .filter((file): file is File => !!file);
    handleCertificateImageFilesChange(files);

    setDeletedCertificateIds((prev) => [...prev, id]);
  };

  const onCertificateChangeById = (
    id: string,
    changed: Partial<CertificateItem>,
  ) => {
    const updated = certificates.map((item) =>
      item.id === id ? { ...item, ...changed } : item,
    );
    setCertificates(updated);

    const finalCertificates = updated.map(({ title, type, id, imageUrl }) => ({
      id,
      title,
      type,
      imageUrl,
    }));
    handleMentoringDataChange({ certificateInfos: finalCertificates });

    const files = updated
      .map((item) => item.file)
      .filter((file): file is File => !!file);
    handleCertificateImageFilesChange(files);
  };

  useEffect(() => {
    const fetchMentoring = async () => {
      if (mentoringId) {
        const { certificates, categories, ...mentoring } =
          await getMentoringDetail(mentoringId);

        const certificateInfosData = certificates.map((info) => ({
          id: info.certificateId,
          title: info.title,
          type: info.type,
          imageUrl: info.imageUrl,
        }));
        const {
          price,
          career,
          introduction,
          content,
          profileImageUrl,
          chatUrl,
        } = mentoring;
        setMentoringData({
          price,
          career,
          introduction,
          content,
          category: categories,
          chatUrl,
          certificateInfos: certificateInfosData,
          profileImageUrl,
        });
        setCertificates(certificateInfosData);

        initialCertificatesIdRef.current = certificates.map(
          (info) => info.certificateId,
        );
      }
    };

    fetchMentoring();
  }, [mentoringId]);

  return (
    <S_Container onSubmit={handleSubmitButtonClick}>
      {!isInitialMentoringData(mentoringData) ? (
        <>
          <BaseInfoSection
            onBaseInfoChange={handleMentoringDataChange}
            priceErrorMessage={priceErrorMessage}
            price={mentoringData.price}
            chatUrlErrorMessage={chatUrlErrorMessage}
            chatUrl={mentoringData.chatUrl}
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
            certificates={certificates}
            onAddButtonClick={onAddButtonClick}
            onCertificateChangeById={onCertificateChangeById}
            onDeleteButtonClick={onDeleteButtonClick}
          />
          <DetailIntroduce
            detailIntroduce={mentoringData.content}
            onDetailIntroduceChange={handleMentoringDataChange}
            detailErrorMessage={detailErrorMessage}
          />
          <S_Separator />
          <ButtonSection
            submitButtonName="update"
            onCancelButtonClick={handleCancelButtonClick}
          />
        </>
      ) : (
        <div>로딩중</div>
      )}
    </S_Container>
  );
}

export default MentoringUpdateForm;

const S_Container = styled.form`
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

const S_Separator = styled.div`
  width: 100%;
  height: 0.1rem;

  background-color: ${({ theme }) => theme.OUTLINE.REGULAR};
`;
