import { useState } from 'react';

import styled from '@emotion/styled';

import CertificatesImageModal from '../CertificatesImageModal/CertificatesImageModal';

import type { Certificates } from '../../../../common/types/MentoringDetail';

interface CertificatesProps {
  certificates: Certificates[];
}

function Certificates({ certificates }: CertificatesProps) {
  const [opened, setOpened] = useState(false);

  const handleModalCloseClick = () => {
    setOpened(false);
  };

  const [selectedCertificate, setSelectedCertificate] =
    useState<Certificates | null>(null);

  const handleItemClick = (certificate: Certificates) => {
    setSelectedCertificate(certificate);
    setOpened(true);
  };

  const validateCertificatesLength = () => {
    const MAX_LENGTH = certificates.length;
    if (!MAX_LENGTH || !selectedCertificate) {
      return false;
    }
    return true;
  };

  const handleNextButtonClick = () => {
    if (!validateCertificatesLength()) {
      return;
    }

    const currentIndex = certificates.findIndex(
      (certificate) => certificate === selectedCertificate,
    );

    const nextIndex = (currentIndex + 1) % certificates.length;
    setSelectedCertificate(certificates[nextIndex]);
  };

  const handlePrevButtonClick = () => {
    if (!validateCertificatesLength()) {
      return;
    }

    const currentIndex = certificates.findIndex(
      (certificate) => certificate === selectedCertificate,
    );

    const prevIndex =
      currentIndex - 1 < 0 ? certificates.length - 1 : currentIndex - 1;
    setSelectedCertificate(certificates[prevIndex]);
  };

  return (
    <S_Container>
      <S_Title>검증된 자격 사항</S_Title>
      {certificates.length > 0 ? (
        <S_List>
          {certificates.map((item) => (
            <S_Item
              key={item.certificateId}
              onClick={() => handleItemClick(item)}
            >
              {item.title}
            </S_Item>
          ))}
        </S_List>
      ) : (
        <S_EmptyDescription>자격증이 없습니다.</S_EmptyDescription>
      )}
      {selectedCertificate && (
        <CertificatesImageModal
          opened={opened}
          onCloseClick={handleModalCloseClick}
          imageUrl={selectedCertificate.imageUrl}
          title={selectedCertificate.title}
          onNextButtonClick={handleNextButtonClick}
          onPrevButtonClick={handlePrevButtonClick}
        />
      )}
    </S_Container>
  );
}

export default Certificates;

const S_Container = styled.section`
  display: flex;
  flex-direction: column;
  gap: 1rem;

  width: 100%;
`;

const S_Title = styled.h3`
  flex-grow: 1;

  color: ${({ theme }) => theme.FONT.B01};
  ${({ theme }) => theme.TYPOGRAPHY.H4_R}
`;

const S_List = styled.ul`
  display: flex;
  flex-direction: column;
  gap: 1.2rem;
`;

const S_Item = styled.li`
  display: flex;
  align-items: flex-start;
  gap: 1.2rem;

  padding: 1.6rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.LIGHT};
  border-radius: 12px;

  background: ${({ theme }) => theme.BG.WHITE};
  transition: all 0.2s ease;
  cursor: pointer;

  &:hover {
    border-color: ${({ theme }) => theme.SYSTEM.MAIN300};

    box-shadow: 0 2px 8px rgb(15 118 110 / 10%);
  }

  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;

const S_EmptyDescription = styled.p`
  color: ${({ theme }) => theme.FONT.B02};
  ${({ theme }) => theme.TYPOGRAPHY.B3_R}
`;
