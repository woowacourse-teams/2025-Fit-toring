import React from 'react';

import styled from '@emotion/styled';

import closeIcon from '../../../../common/assets/images/close.svg';

interface ProfileImageModalProps {
  opened: boolean;
  imageSrc: string;
  onCloseClick: () => void;
}

function ProfileImageModal({
  opened,
  imageSrc,
  onCloseClick,
}: ProfileImageModalProps) {
  return opened ? (
    <S_Contaienr>
      <S_CloseButtonWrapper>
        <S_CloseButton onClick={onCloseClick}>
          <S_CloseIcon src={closeIcon} alt="닫기 아이콘" />
        </S_CloseButton>
      </S_CloseButtonWrapper>
      <S_ImgWrapper>
        <S_Img src={imageSrc} alt="멘토 프로필 이미지" />
      </S_ImgWrapper>
    </S_Contaienr>
  ) : null;
}

export default ProfileImageModal;

const S_Contaienr = styled.div`
  display: flex;
  flex-direction: column;
  width: 48rem;
  height: 100vh;
  background-color: ${({ theme }) => theme.BG.BLACK};

  position: fixed;
  top: 0;
  left: 50%;
  transform: translateX(-50%);
  z-index: 100;

  @media screen and (width <= 480px) {
    width: 100%;
  }
`;

const S_CloseButtonWrapper = styled.div`
  position: absolute;
  width: 100%;
  height: 6rem;
  background: linear-gradient(
    to bottom,
    rgba(0, 0, 0, 0.1) 0%,
    rgba(0, 0, 0, 0.05) 50%,
    transparent 100%
  );
`;

const S_CloseButton = styled.button`
  border: none;
  background-color: transparent;
  padding: 1rem;
  cursor: pointer;
  position: absolute;
`;
const S_CloseIcon = styled.img`
  width: 2.4rem;
  aspect-ratio: 1/1;
`;

const S_ImgWrapper = styled.div`
  display: flex;
  align-items: center;
  justify-content: center;
  flex-grow: 1;
`;

const S_Img = styled.img`
  width: 100%;
`;
