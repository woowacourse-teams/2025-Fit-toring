import { css } from '@emotion/react';
import styled from '@emotion/styled';

import notBlind from '../../../../common/assets/images/notBlind.svg';
import Button from '../../../../common/components/Button/Button';
import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';

function SignupForm() {
  return (
    <StyledContainer>
      <StyledNameAndGenderWrapper>
        <FormField label="이름 *" htmlFor="name">
          <StyledNameInputWrapper>
            <Input id="name" placeholder="홍길동" />
          </StyledNameInputWrapper>
        </FormField>
        <fieldset>
          <StyledLegend>성별 *</StyledLegend>
          <StyledRadios>
            <StyledRadioWrapper>
              <StyledLabel htmlFor="male">남</StyledLabel>
              <StyledRadio type="radio" name="gender" value="male" id="male" />
            </StyledRadioWrapper>
            <StyledRadioWrapper>
              <StyledLabel htmlFor="female">여</StyledLabel>
              <StyledRadio
                type="radio"
                name="gender"
                value="female"
                id="female"
              />
            </StyledRadioWrapper>
          </StyledRadios>
        </fieldset>
      </StyledNameAndGenderWrapper>

      <FormField label="아이디 *" htmlFor="id">
        <StyledInputAndBtnWrapper>
          <div className="input-wapper">
            <Input id="id" placeholder="fittoring" />
          </div>
          <Button type="button" customStyle={buttonCustomStyle}>
            중복확인
          </Button>
        </StyledInputAndBtnWrapper>
      </FormField>

      <FormField label="비밀번호 *" htmlFor="password">
        <StyledInputWrapper>
          <StyledInput
            id="password"
            placeholder="5자이상 15자이하 입력하세요"
          />
          <StyledImg src={notBlind} />
        </StyledInputWrapper>
      </FormField>
      <FormField label="비밀번호 확인*" htmlFor="passwordConfrim">
        <StyledInputWrapper>
          <StyledInput
            id="passwordConfrim"
            placeholder="비밀번호를 다시 입력하세요"
          />
          <StyledImg src={notBlind} />
        </StyledInputWrapper>
      </FormField>

      <FormField label="전화번호 *" htmlFor="phone">
        <StyledInputAndBtnWrapper>
          <div className="input-wapper">
            <Input id="phone" placeholder="010-1234-5678" type="tel" />
          </div>
          <Button type="button" customStyle={buttonCustomStyle}>
            인증요청
          </Button>
        </StyledInputAndBtnWrapper>
      </FormField>
      <FormField label="인증번호 확인 *" htmlFor="verificationCode">
        <StyledInputAndBtnWrapper>
          <div className="input-wapper">
            <Input id="verificationCode" placeholder="123456" type="tel" />
          </div>
          <Button type="button" customStyle={buttonCustomStyle}>
            인증하기
          </Button>
        </StyledInputAndBtnWrapper>
      </FormField>
      <Button
        type="submit"
        size="full"
        customStyle={css`
          height: 5.2rem;

          font-size: 1.6rem;
        `}
      >
        회원가입
      </Button>
    </StyledContainer>
  );
}

export default SignupForm;

const StyledContainer = styled.form`
  display: flex;
  flex-direction: column;
  gap: 1.7rem;

  padding: 2.8rem 3.3rem;

  background-color: ${({ theme }) => theme.BG.WHITE};
`;

const StyledNameAndGenderWrapper = styled.div`
  grid-template-columns: 1fr 1fr;

  display: grid;
  gap: 2rem;
`;

const StyledNameInputWrapper = styled.div`
  height: 4rem;
`;

const StyledInputAndBtnWrapper = styled.div`
  display: flex;
  gap: 1.4rem;

  & > .input-wapper {
    flex-grow: 1;
  }
`;

const StyledInputWrapper = styled.div<{ errored?: boolean }>`
  display: flex;

  width: 100%;
  height: 100%;
  height: 4rem;
  border: ${({ theme, errored }) =>
      errored ? theme.FONT.ERROR : theme.OUTLINE.DARK}
    0.1rem solid;
  border-radius: 0.7rem;

  background-color: white;
`;

const StyledInput = styled.input`
  flex-grow: 1;

  padding: 0.7rem 1.1rem;
  border: none;

  background-color: transparent;

  :focus {
    outline: none;
  }

  ::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
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

  width: 13px;
  height: 13px;
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
  color: ${({ theme }) => theme.FONT.B02};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
  cursor: pointer;
`;

const StyledImg = styled.img`
  width: 2rem;
  margin: 0.8rem;
  cursor: pointer;
`;

const buttonCustomStyle = css`
  height: 4rem;
  padding: 1.1rem 0.8rem;

  font-size: 1.4rem;
`;
