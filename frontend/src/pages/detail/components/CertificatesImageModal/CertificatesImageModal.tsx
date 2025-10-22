import { useRef } from 'react';

import styled from '@emotion/styled';
import { createPortal } from 'react-dom';

import prevImg from '../../../../common/assets/images/chevron-left.svg';
import nextImg from '../../../../common/assets/images/chevron-right.svg';
import closeImg from '../../../../common/assets/images/white-x-mark.svg';
import Modal from '../../../../common/components/Modal/Modal';
import { useFocusTrap } from '../../../../common/hooks/useFocusTrap';


interface CertificatesImageModalProps {
  opened: boolean;
  onCloseClick: () => void;
  imageUrl: string;
  title: string;
  onNextButtonClick: () => void;
  onPrevButtonClick: () => void;
}

function CertificatesImageModal({
  opened,
  onCloseClick,
  imageUrl,
  title,
  onNextButtonClick,
  onPrevButtonClick,
}: CertificatesImageModalProps) {
  const containerRef = useRef<HTMLDivElement>(null);
  useFocusTrap(containerRef, opened, onCloseClick);
  return createPortal(
    <Modal opened={opened} onCloseClick={onCloseClick}>
      <S_Container
        role="region"
        aria-label="자격증 이미지 미리보기"
        ref={containerRef}
      >
        <S_CloseButton onClick={onCloseClick}>
          <S_CloseImage src={closeImg} alt="모달 닫기 버튼" />
        </S_CloseButton>
        <S_PrevButton onClick={onPrevButtonClick}>
          <S_PrevImage src={prevImg} alt="이전 이미지 버튼" />
        </S_PrevButton>
        <S_Image aria-live="polite" src={imageUrl} alt={`${title}`} />
        <S_NextButton onClick={onNextButtonClick}>
          <S_NextImage src={nextImg} alt="다음 이미지 버튼" />
        </S_NextButton>
      </S_Container>
    </Modal>,
    document.body,
  );
}

export default CertificatesImageModal;

const S_Container = styled.div`
  position: relative;
`;

const S_Image = styled.img`
  display: flex;
  align-items: center;
  justify-content: center;

  width: 100%;
  height: 100%;
  max-height: 65vh;
  object-fit: cover;
`;

const S_CloseButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: -6rem;
  right: -2rem;

  padding: 0;
  border: none;

  background: none;
  cursor: pointer;
`;

const S_CloseImage = styled.img`
  width: 3rem;
  height: 3rem;
`;

const S_PrevButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 50%;
  left: -17%;

  padding: 0;
  border: none;

  background: none;
  cursor: pointer;
`;

const S_PrevImage = styled.img`
  width: 3rem;
  height: 3rem;
`;

const S_NextButton = styled.button`
  display: flex;
  align-items: center;
  justify-content: center;
  position: absolute;
  top: 50%;
  right: -17%;

  padding: 0;
  border: none;

  background: none;
  cursor: pointer;
`;

const S_NextImage = styled.img`
  width: 3rem;
  height: 3rem;
`;
