import { useEffect, useState } from 'react';

import styled from '@emotion/styled';

import { getSpecialties } from '../../../../common/apis/getSpecialties';
import SpecialtyTag from '../SpecialtyTag/SpecialtyTag';
import TitleSeparator from '../TitleSeparator/TitleSeparator';

import type { Specialty } from '../../../../common/types/Specialty';


function SpecialtySection() {

  return (
    <section>
      <TitleSeparator>전문 분야</TitleSeparator>
      <StyledGuideText>최대 3개까지 등록 가능합니다.</StyledGuideText>
      <StyledSpecialtyWrapper>
          />
        ))}
      </StyledSpecialtyWrapper>
    </section>
  );
}

export default SpecialtySection;

const StyledSpecialtyWrapper = styled.div`
  display: flex;
  flex-wrap: wrap;
  gap: 1.2rem;

  width: 100%;
  height: 100%;
`;

const StyledGuideText = styled.p`
  margin-bottom: 2rem;
  padding-left: 0.5rem;

  ${({ theme }) => theme.TYPOGRAPHY.B4_R}
  color: ${({ theme }) => theme.FONT.B04}
`;
