import { useEffect, useRef, useState } from 'react';

import styled from '@emotion/styled';

import defaultProfileImg from '../../../common/assets/images/profileImg.svg';
import useS3Upload from '../../../common/hooks/useS3Upload';
import { convertHeicToJpegIfNeeded } from '../../../common/utils/heicFile/convertHeicToJpegIfNeeded';

import ProfileImageActionSheet from './ProfileImageActionSheet';

interface EditProfileImageFieldProps {
  initialImageUrl: string | null;
  onProfileImageKeyChange: (profileImageKey: string | undefined) => void;
  onImageProcessingChange: (isImageProcessing: boolean) => void;
}

function EditProfileImageField({
  initialImageUrl,
  onProfileImageKeyChange,
  onImageProcessingChange,
}: EditProfileImageFieldProps) {
  const [previewUrl, setPreviewUrl] = useState<string | null>(initialImageUrl);
  const [imageErrored, setImageErrored] = useState(false);
  const [actionSheetOpened, setActionSheetOpened] = useState(false);
  const [isImageProcessing, setIsImageProcessing] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const objectUrlRef = useRef<string | null>(null);
  const locallyChangedRef = useRef(false);

  const { uploadFile } = useS3Upload();

  useEffect(() => {
    return () => {
      if (objectUrlRef.current) {
        URL.revokeObjectURL(objectUrlRef.current);
      }
    };
  }, []);

  useEffect(() => {
    if (locallyChangedRef.current) {
      return;
    }

    setPreviewUrl(initialImageUrl);
    setImageErrored(false);
  }, [initialImageUrl]);

  const hasProfileImage = previewUrl !== null;

  const updateImageProcessing = (nextIsImageProcessing: boolean) => {
    setIsImageProcessing(nextIsImageProcessing);
    onImageProcessingChange(nextIsImageProcessing);
  };

  const closeActionSheet = () => {
    setActionSheetOpened(false);
  };

  const handlePlusButtonClick = () => {
    setActionSheetOpened(true);
  };

  const handleAlbumSelectClick = () => {
    closeActionSheet();
    fileInputRef.current?.click();
  };

  const handleProfileImageDeleteClick = () => {
    if (objectUrlRef.current) {
      URL.revokeObjectURL(objectUrlRef.current);
      objectUrlRef.current = null;
    }

    setPreviewUrl(null);
    setImageErrored(false);
    locallyChangedRef.current = true;
    onProfileImageKeyChange(initialImageUrl ? '' : undefined);
    closeActionSheet();
  };

  const validatePreviewImage = (imageUrl: string) => {
    return new Promise<void>((resolve, reject) => {
      const image = new Image();

      image.onload = () => resolve();
      image.onerror = () => reject(new Error('이미지를 불러올 수 없습니다.'));
      image.src = imageUrl;
    });
  };

  const handleProfileImageInputChange = async (
    e: React.ChangeEvent<HTMLInputElement>,
  ) => {
    const files = e.target.files;
    const file = files?.[0];

    if (files && files.length > 1) {
      alert('이미지는 최대 1장까지 첨부할 수 있어요');
      e.target.value = '';
      return;
    }

    if (!file) {
      return;
    }

    const previousPreviewUrl = previewUrl;
    const previousImageErrored = imageErrored;
    const previousObjectUrl = objectUrlRef.current;

    try {
      updateImageProcessing(true);

      const convertedFile = await convertHeicToJpegIfNeeded(file);
      const objectUrl = URL.createObjectURL(convertedFile);

      objectUrlRef.current = objectUrl;
      await validatePreviewImage(objectUrl);

      setPreviewUrl(objectUrl);
      setImageErrored(false);

      const { uploadedKey } = await uploadFile(convertedFile, 'MEMBER_PROFILE');

      if (!uploadedKey) {
        throw new Error('업로드된 이미지 key가 유효하지 않습니다.');
      }

      if (previousObjectUrl) {
        URL.revokeObjectURL(previousObjectUrl);
      }

      onProfileImageKeyChange(uploadedKey);
      locallyChangedRef.current = true;
    } catch (error) {
      if (objectUrlRef.current && objectUrlRef.current !== previousObjectUrl) {
        URL.revokeObjectURL(objectUrlRef.current);
      }

      objectUrlRef.current = previousObjectUrl;
      setPreviewUrl(previousPreviewUrl);
      setImageErrored(previousImageErrored);

      console.error('회원 프로필 이미지 업로드 실패', error);
      alert(
        error instanceof Error && error.message === '이미지를 불러올 수 없습니다.'
          ? '이미지를 불러올 수 없어요'
          : '프로필 이미지 업로드에 실패했습니다. 다시 시도해주세요.',
      );
    } finally {
      updateImageProcessing(false);
      e.target.value = '';
    }
  };

  return (
    <S_Container>
      <S_ImageButton
        type="button"
        onClick={handlePlusButtonClick}
        aria-label="프로필 이미지 변경 메뉴 열기"
        disabled={isImageProcessing}
      >
        <S_ProfileImage
          src={previewUrl && !imageErrored ? previewUrl : defaultProfileImg}
          alt="프로필 이미지"
          onError={() => setImageErrored(true)}
        />
        <S_CameraBadge aria-hidden="true">
          <S_CameraIcon />
        </S_CameraBadge>
      </S_ImageButton>
      <S_HiddenInput
        ref={fileInputRef}
        type="file"
        accept="image/*"
        multiple
        onChange={handleProfileImageInputChange}
      />
      <ProfileImageActionSheet
        opened={actionSheetOpened}
        showDeleteButton={hasProfileImage}
        onAlbumSelectClick={handleAlbumSelectClick}
        onDeleteClick={handleProfileImageDeleteClick}
        onCloseClick={closeActionSheet}
      />
    </S_Container>
  );
}

export default EditProfileImageField;

const S_Container = styled.div`
  display: flex;
  justify-content: center;
`;

const S_ImageButton = styled.button`
  position: relative;

  width: 11.2rem;
  height: 11.2rem;
  padding: 0;
  border: none;

  background: none;

  cursor: pointer;

  &:disabled {
    cursor: wait;
    opacity: 0.7;
  }
`;

const S_ProfileImage = styled.img`
  width: 100%;
  height: 100%;
  border-radius: 50%;

  background-color: ${({ theme }) => theme.SYSTEM.GRAY50};
  object-fit: cover;
`;

const S_CameraBadge = styled.span`
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  right: 0.2rem;
  bottom: 0.2rem;

  width: 3.2rem;
  height: 3.2rem;
  border-radius: 50%;
  box-shadow: 0 0.1rem 0.4rem rgb(0 0 0 / 10%);

  background-color: ${({ theme }) => theme.SYSTEM.GRAY200};
`;

const S_CameraIcon = styled.span`
  position: relative;

  width: 1.8rem;
  height: 1.3rem;
  border-radius: 0.3rem;

  background-color: ${({ theme }) => theme.BG.WHITE};

  &::before {
    position: absolute;
    top: -0.4rem;
    left: 0.5rem;

    width: 0.8rem;
    height: 0.4rem;
    border-radius: 0.2rem 0.2rem 0 0;

    background-color: ${({ theme }) => theme.BG.WHITE};

    content: '';
  }

  &::after {
    position: absolute;
    top: 0.3rem;
    left: 0.55rem;

    width: 0.7rem;
    height: 0.7rem;
    border-radius: 50%;

    background-color: ${({ theme }) => theme.SYSTEM.GRAY200};

    content: '';
  }
`;

const S_HiddenInput = styled.input`
  display: none;
`;
