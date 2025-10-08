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
import { addSentryBreadcrumb } from '../../../../common/utils/addSentryBreadcrumb';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { careerValidator } from '../../../../common/utils/careerValidator';
import { introduceValidator } from '../../../../common/utils/introduceValidator';
import { priceValidator } from '../../../../common/utils/priceValidator';
import { validateChatUrl } from '../../../../common/utils/validateChatUrl';
import { validateTextarea } from '../../../../common/utils/validateDetail';
import { postMentoringCreate } from '../../apis/postMentoringCreate';

import type { CertificateItem } from '../../../../common/types/certificateItem';
import type { mentoringCreateFormData } from '../../../../common/types/mentoringCreateFormData';

function MentoringCreateForm() {
  const [mentoringData, setMentoringData] = useState<mentoringCreateFormData>({
    price: 0,
    category: [],
    introduction: '',
    career: 0,
    content: '',
    chatUrl: '',
    certificateInfos: [],
  });
  const [profileImageFile, setProfileImageFile] = useState<File | null>(null);
  const [certificateImageFiles, setCertificateImageFiles] = useState<File[]>(
    [],
  );

  const priceErrorMessage = priceValidator(mentoringData.price);
  const introduceErrorMessage = introduceValidator(mentoringData.introduction);
  const careerErrorMessage = careerValidator(mentoringData.career);
  const chatUrlErrorMessage = validateChatUrl(mentoringData.chatUrl);
  const detailErrorMessage = validateTextarea(mentoringData.content);
  const handleMentoringDataChange = (
    newData: Partial<mentoringCreateFormData>,
  ) => {
    setMentoringData((prevData) => ({
      ...prevData,
      ...newData,
    }));

    addSentryBreadcrumb({
      category: 'ui.change',
      message: `멘토링 데이터 변경`,
      data: { newData },
    });
  };

  const handleProfileImageChange = (file: File | null) => {
    setProfileImageFile(file);

    addSentryBreadcrumb({
      category: 'ui.change',
      message: `프로필 이미지 변경`,
      data: { file },
    });
  };

  const handleCertificateImageFilesChange = (files: File[]) => {
    setCertificateImageFiles(files);

    addSentryBreadcrumb({
      category: 'ui.change',
      message: `자격증 이미지 변경`,
      data: { files },
    });
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
        level: 'error',
        feature: 'mentoring',
        step: 'mentoring-create',
        extras: {
          mentoringData: {
            ...mentoringData,
            certificateInfos: filteredCertificateInfos,
          },
          profileImageFile,
          certificateImageFiles,
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
    navigate(PAGE_URL.HOME);

    addSentryBreadcrumb({
      category: 'ui.submit',
      message: '멘토링 생성 폼 제출 시도',
      data: {
        isFormValid:
          priceErrorMessage ||
          introduceErrorMessage ||
          careerErrorMessage ||
          chatUrlErrorMessage,
      },
    });
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

    addSentryBreadcrumb({
      category: 'ui.click',
      message: '자격증 항목 추가',
      data: { newTotalCertificates: certificates.length + 1 },
    });
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

    addSentryBreadcrumb({
      category: 'ui.click',
      message: '자격증 항목 삭제',
      data: {
        deletedCertificateId: id,
        newTotalCertificates: certificates.length - 1,
      },
    });
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

  return (
    <S_Container onSubmit={handleSubmitButtonClick}>
      <BaseInfoSection
        onBaseInfoChange={handleMentoringDataChange}
        priceErrorMessage={priceErrorMessage}
        price={mentoringData.price}
        chatUrlErrorMessage={chatUrlErrorMessage}
        chatUrl={mentoringData.chatUrl}
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
        onCancelButtonClick={handleCancelButtonClick}
        submitButtonName="register"
      />
    </S_Container>
  );
}

export default MentoringCreateForm;

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
