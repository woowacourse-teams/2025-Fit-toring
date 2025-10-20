import { useState } from 'react';

import styled from '@emotion/styled';
import { useMutation } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import BaseInfoSection from '../../../../common/components/mentoringForm/BaseInfoSection/BaseInfoSection';
import ButtonSection from '../../../../common/components/mentoringForm/ButtonSection/ButtonSection';
import CertificateSection from '../../../../common/components/mentoringForm/CertificateSection/CertificateSection';
import DetailIntroduce from '../../../../common/components/mentoringForm/DetailIntroduce/DetailIntroduce';
import IntroduceSection from '../../../../common/components/mentoringForm/IntroduceSection/IntroduceSection';
import ProfileSection from '../../../../common/components/mentoringForm/ProfileSection/ProfileSection';
import SpecialtySection from '../../../../common/components/mentoringForm/SpecialtySection/SpecialtySection';
import { PAGE_URL } from '../../../../common/constants/url';
import useS3Upload from '../../../../common/hooks/useS3Upload';
import { addSentryBreadcrumb } from '../../../../common/utils/addSentryBreadcrumb';
import { captureSentryError } from '../../../../common/utils/captureSentryError';
import { careerValidator } from '../../../../common/utils/careerValidator';
import { introduceValidator } from '../../../../common/utils/introduceValidator';
import { priceValidator } from '../../../../common/utils/priceValidator';
import { validateTextarea } from '../../../../common/utils/validateDetail';
import { postMentoringCreate } from '../../apis/postMentoringCreate';

import type { CertificateItem } from '../../../../common/types/certificateItem';
import type { mentoringCreateFormData } from '../../../../common/types/mentoringCreateFormData';

function MentoringCreateForm() {
  const [mentoringData, setMentoringData] = useState<mentoringCreateFormData>({
    price: 0,
    category: [],
    introduction: '',
    profileImageUrl: null,
    career: 0,
    content: '',
    certificateInfoRequests: [],
  });

  const priceErrorMessage = priceValidator(mentoringData.price);
  const introduceErrorMessage = introduceValidator(mentoringData.introduction);
  const careerErrorMessage = careerValidator(mentoringData.career);
  const detailErrorMessage = validateTextarea(mentoringData.content);

  const { uploadFile: uploadImageFile } = useS3Upload();

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

  const handleProfileImageChange = async (file: File | null) => {
    if (!file) {
      handleMentoringDataChange({
        profileImageUrl: null,
      });
      addSentryBreadcrumb({
        category: 'ui.change',
        message: `프로필 이미지 제거`,
      });
      return;
    }

    try {
      const { uploadedUrl } = await uploadImageFile(file, 'MENTORING_PROFILE');

      if (!uploadedUrl || uploadedUrl === '') {
        throw new Error('업로드된 URL이 유효하지 않습니다.');
      }

      handleMentoringDataChange({
        profileImageUrl: uploadedUrl,
      });

      addSentryBreadcrumb({
        category: 'ui.change',
        message: `프로필 이미지 변경 성공`,
        data: { fileName: file.name, fileSize: file.size },
      });
    } catch (error) {
      const errorMessage =
        error instanceof Error
          ? error.message
          : '프로필 이미지 업로드 중 알 수 없는 오류가 발생했습니다.';

      console.error('프로필 이미지 업로드 실패:', errorMessage);
      alert('프로필 이미지 업로드에 실패했습니다. 다시 시도해주세요.');

      addSentryBreadcrumb({
        category: 'ui.error',
        message: `프로필 이미지 업로드 실패`,
        data: {
          fileName: file.name,
          fileSize: file.size,
          error: errorMessage,
        },
        level: 'error',
      });
    }
  };

  const mutation = useMutation({
    mutationFn: postMentoringCreate,
    onSuccess: (response) => {
      if (response.status === 201) {
        alert('멘토링 등록 성공');
      }
    },
    onError: (error, variables) => {
      console.error('멘토링 등록 실패');
      captureSentryError({
        error,
        level: 'error',
        feature: 'mentoring',
        step: 'mentoring-create',
        extras: {
          ...variables,
        },
      });
    },
  });

  const submitMentoringForm = async () => {
    mutation.mutate({
      mentoringData: {
        ...mentoringData,
      },
    });
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
          priceErrorMessage || introduceErrorMessage || careerErrorMessage,
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
        imageUrl: undefined,
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

    const finalCertificates = updated.map(({ title, type, imageUrl }) => ({
      title,
      type,
      imageUrl,
    }));
    handleMentoringDataChange({ certificateInfoRequests: finalCertificates });

    addSentryBreadcrumb({
      category: 'ui.click',
      message: '자격증 항목 삭제',
      data: {
        deletedCertificateId: id,
        newTotalCertificates: certificates.length - 1,
      },
    });
  };

  const onCertificateChangeById = async (
    id: string,
    changed: Partial<CertificateItem>,
  ) => {
    if (changed.file) {
      try {
        const { uploadedUrl } = await uploadImageFile(
          changed.file,
          'CERTIFICATE',
        );

        if (!uploadedUrl || uploadedUrl === '') {
          throw new Error('자격증 이미지 업로드에 실패했습니다.');
        }

        changed.imageUrl = uploadedUrl === '' ? null : uploadedUrl;

        addSentryBreadcrumb({
          category: 'ui.change',
          message: '자격증 이미지 업로드 성공',
          data: {
            certificateId: id,
            fileName: changed.file.name,
            fileSize: changed.file.size,
          },
        });
      } catch (error) {
        const errorMessage =
          error instanceof Error
            ? error.message
            : '자격증 이미지 업로드 중 알 수 없는 오류가 발생했습니다.';

        console.error('자격증 이미지 업로드 실패:', errorMessage);
        alert('자격증 이미지 업로드에 실패했습니다. 다시 시도해주세요.');

        addSentryBreadcrumb({
          category: 'ui.error',
          message: '자격증 이미지 업로드 실패',
          data: {
            certificateId: id,
            fileName: changed.file?.name,
            fileSize: changed.file?.size,
            error: errorMessage,
          },
          level: 'error',
        });

        return;
      }
    }

    const updated = certificates.map((item) =>
      item.id === id ? { ...item, ...changed } : item,
    );

    setCertificates(updated);

    const finalCertificates = updated.map(({ title, type, imageUrl }) => ({
      title,
      type,
      imageUrl,
    }));
    handleMentoringDataChange({ certificateInfoRequests: finalCertificates });
  };

  return (
    <S_Container onSubmit={handleSubmitButtonClick}>
      <BaseInfoSection
        onBaseInfoChange={handleMentoringDataChange}
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
