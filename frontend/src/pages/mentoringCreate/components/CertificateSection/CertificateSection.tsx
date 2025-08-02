import styled from '@emotion/styled';

import TitleSeparator from '../TitleSeparator/TitleSeparator';

function CertificateSection() {
  return (
    <section>
      <TitleSeparator>검증된 자격 사항</TitleSeparator>
      <StyledGuideText>최대 3개까지 등록 가능합니다.</StyledGuideText>
      <StyledDescriptionWrapper>
        <p>증명서 또는 관련 사진이 확인된 후 게시됩니다.</p>
        <p>항목 작성 후 게시요청 해주세요.</p>
        <p>
          승인 또는 반려 결과에 대해 앱 알림 으로 결과를 알려드리며, 필요 시
          멘토님께 직접 연락 드립니다.
        </p>
        <p>멘토 페이지에는 항목 형식에 따라 순서대로 보여집니다.</p>
      </StyledDescriptionWrapper>
    </section>
  );
}

export default CertificateSection;

const StyledGuideText = styled.p`
  margin-bottom: 2rem;
  padding-left: 0.5rem;

  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
  color: ${({ theme }) => theme.FONT.B04}
`;

const StyledDescriptionWrapper = styled.div`
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  justify-content: center;
  gap: 1.4rem;

  width: 100%;
  height: 100%;
  padding: 2rem;
  border: 1px solid ${({ theme }) => theme.OUTLINE.REGULAR};
  border-radius: 1.2rem;

  background-color: ${({ theme }) => theme.BG.LIGHT};

  & > p {
    ${({ theme }) => theme.TYPOGRAPHY.B2_R}
    color: ${({ theme }) => theme.FONT.B04};
  }
`;
