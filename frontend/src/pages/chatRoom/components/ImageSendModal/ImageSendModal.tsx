import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import Button from '../../../../common/components/Button/Button';
import Modal from '../../../../common/components/Modal/Modal';

interface ImageSendModalProps {
  selectedImage: File;
  onSend: () => void;
  onCancel: () => void;
}

function ImageSendModal({
  selectedImage,
  onSend,
  onCancel,
}: ImageSendModalProps) {
  const [previewUrl, setPreviewUrl] = useState('');

  useEffect(() => {
    const url = URL.createObjectURL(selectedImage);
    setPreviewUrl(url);

    return () => {
      URL.revokeObjectURL(url);
    };
  }, [selectedImage]);

  return (
    <Modal opened={true} onCloseClick={onCancel}>
      <S_Container>
        <S_Title>파일 전송</S_Title>
        {previewUrl && <S_Preview src={previewUrl} alt="미리보기" />}
        <S_ButtonWrapper>
          <S_CancelButton onClick={onCancel} size="full">
            취소
          </S_CancelButton>
          <S_SendButton onClick={onSend} size="full">
            전송
          </S_SendButton>
        </S_ButtonWrapper>
      </S_Container>
    </Modal>
  );
}

export default ImageSendModal;

const S_Container = styled.div`
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.8rem;
`;

const S_Title = styled.h3`
  ${({ theme }) => theme.TYPOGRAPHY.H3_R};
`;

const S_Preview = styled.img`
  width: 100%;
  height: auto;
  aspect-ratio: 1 / 1;
  object-fit: cover;
`;

const S_ButtonWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.4rem;

  width: 100%;
  padding: 0.4rem 0;
`;

const S_CancelButton = styled(Button)`
  border: 1px solid ${({ theme }) => theme.OUTLINE.BLACK};

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.FONT.B01};
`;

const S_SendButton = styled(Button)`
  border: 1px solid ${({ theme }) => theme.OUTLINE.BLACK};

  background-color: ${({ theme }) => theme.BG.BLACK};
`;
