import styled from '@emotion/styled';

import CertificateInput from '../CertificateInput/CertificateInput';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

import type { CertificateItem } from '../../../types/certificateItem';

interface CertificateSectionProps {
  onAddButtonClick: () => void;
  onDeleteButtonClick: (id: string) => void;
  onCertificateChangeById: (
    id: string,
    changed: Partial<CertificateItem>,
  ) => void;
  certificates: CertificateItem[];
}

function CertificateSection({
  certificates,
  onAddButtonClick,
  onDeleteButtonClick,
  onCertificateChangeById,
}: CertificateSectionProps) {
  return (
    <section>
      <TitleSeparator>검증된 자격 사항</TitleSeparator>
      <StyledDescriptionWrapper>
        <p>증명서 또는 관련 사진이 확인된 후 게시됩니다.</p>
        <p>항목 작성 후 게시요청 해주세요.</p>
        <p>
          승인 또는 반려 결과에 대해 앱 알림 으로 결과를 알려드리며, 필요 시
          멘토님께 직접 연락 드립니다.
        </p>
        <p>멘토 페이지에는 항목 형식에 따라 순서대로 보여집니다.</p>
      </StyledDescriptionWrapper>
      {certificates.map((item) => (
        <CertificateInput
          key={item.id}
          id={item.id}
          onDeleteButtonClick={() => onDeleteButtonClick(item.id)}
          onCertificateChange={onCertificateChangeById}
          onCertificateImageFileChange={(file) =>
            onCertificateChangeById(item.id, {
              file,
              imageUrl: item.imageUrl,
            })
          }
          certificateInfo={item}
        />
      ))}

      <StyledAddButton type="button" onClick={onAddButtonClick}>
        + 자격 항목 추가하기
      </StyledAddButton>
    </section>
  );
}

export default CertificateSection;

const StyledDescriptionWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 1.4rem;

  width: 100%;
  margin-bottom: 3.5rem;
  padding: 2rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 12px;

  background-color: ${({ theme }) => theme.BG.LIGHT};

  & > p {
    ${({ theme }) => theme.TYPOGRAPHY.B2_R}
    color: ${({ theme }) => theme.FONT.B04};
  }
`;

const StyledAddButton = styled.button`
  width: 100%;
  height: 6.8rem;
  margin-top: 1.5rem;
  border: 1px dashed ${({ theme }) => theme.SYSTEM.MAIN600};
  ${({ theme }) => theme.TYPOGRAPHY.BTN1_R}
  border-radius: 12px;

  background-color: ${({ theme }) => theme.BG.WHITE};

  color: ${({ theme }) => theme.SYSTEM.MAIN700};
  cursor: pointer;

  &:active {
    transform: scale(0.98);
  }
`;
