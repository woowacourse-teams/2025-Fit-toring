import styled from '@emotion/styled';

import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';

function UserInfoFields() {
  return (
    <StyledContainer>
      <FormField label="이름 *">
        <StyledNameInputWrapper>
          <Input id="name" placeholder="홍길동" />
        </StyledNameInputWrapper>
      </FormField>
      <fieldset>
        <StyledLegend>성별 *</StyledLegend>
        <StyledRadios>
          <StyledRadioWrapper>
            <StyledLabel>
              남
              <StyledRadio type="radio" name="gender" value="male" id="male" />
            </StyledLabel>
          </StyledRadioWrapper>
          <StyledRadioWrapper>
            <StyledLabel>
              여
              <StyledRadio
                type="radio"
                name="gender"
                value="female"
                id="female"
              />
            </StyledLabel>
          </StyledRadioWrapper>
        </StyledRadios>
      </fieldset>
    </StyledContainer>
  );
}

export default UserInfoFields;

const StyledContainer = styled.div`
  grid-template-columns: 1fr 1fr;

  display: grid;
  gap: 2rem;
`;

const StyledNameInputWrapper = styled.div`
  height: 4rem;
`;

const StyledLegend = styled.legend`
  color: ${({ theme }) => theme.FONT.B02};

  ${({ theme }) => theme.TYPOGRAPHY.B4_R};
`;

const StyledRadioWrapper = styled.div`
  display: flex;
  align-items: center;
  gap: 0.5rem;
`;

const StyledRadios = styled.div`
  display: flex;
  gap: 3rem;

  height: 4rem;
  margin-top: 0.7rem;
`;

const StyledRadio = styled.input`
  flex-shrink: 0;

  width: 1.4rem;
  height: 1.4rem;
  margin: 0;
  outline: none;
  border: 1px solid #ccc;
  border-radius: 50%;
  appearance: none;
  cursor: pointer;

  &:checked {
    border: 3px solid #fff;
    box-shadow: 0 0 0 1px ${({ theme }) => theme.SYSTEM.MAIN600};

    background-color: ${({ theme }) => theme.SYSTEM.MAIN600};
  }
`;

const StyledLabel = styled.label`
  display: flex;
  gap: 1rem;
  align-items: center;

  color: ${({ theme }) => theme.FONT.B02};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  cursor: pointer;
`;
