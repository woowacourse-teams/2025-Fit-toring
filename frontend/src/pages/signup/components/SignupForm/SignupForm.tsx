import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import blind from '../../../../common/assets/images/blind.svg';
import notBlind from '../../../../common/assets/images/notBlind.svg';
import Button from '../../../../common/components/Button/Button';
import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';

function SignupForm() {
  const [passwordVisible, setPasswordVisible] = useState(false);
  const [passwordConfrimVisible, setPasswordConfrimVisible] = useState(false);

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
        <StyledInputWithIconWrapper>
          <StyledInput
            id="password"
            placeholder="5자이상 15자이하 입력하세요"
            type={passwordVisible ? 'text' : 'password'}
          />
          <StyledImg
            src={passwordVisible ? blind : notBlind}
            onClick={() => setPasswordVisible((prev) => !prev)}
          />
        </StyledInputWithIconWrapper>
      </FormField>
      <FormField label="비밀번호 확인*" htmlFor="passwordConfrim">
        <StyledInputWithIconWrapper>
          <StyledInput
            id="passwordConfrim"
            placeholder="비밀번호를 다시 입력하세요"
            type={passwordConfrimVisible ? 'text' : 'password'}
          />
          <StyledImg
            src={passwordConfrimVisible ? blind : notBlind}
            onClick={() => setPasswordConfrimVisible((prev) => !prev)}
          />
        </StyledInputWithIconWrapper>
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
          box-shadow: 0 4px 12px 0 rgb(0 120 111 / 30%);

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

const StyledInputWithIconWrapper = styled.div<{ errored?: boolean }>`
  position: relative;
`;

const StyledInput = styled.input<{ errored?: boolean }>`
  flex-grow: 1;

  width: 100%;
  height: 4rem;
  padding: 0.7rem 1.1rem;
  padding-right: 4rem;
  border: ${({ theme, errored }) =>
      errored ? theme.FONT.ERROR : theme.OUTLINE.DARK}
    0.1rem solid;
  border-radius: 0.7rem;

  background-color: transparent;

  :focus {
    outline: none;
    border: 2px solid ${({ theme }) => theme.SYSTEM.MAIN600};
  }

  ::placeholder {
    color: ${({ theme }) => theme.SYSTEM.GRAY200};
  }

  color: ${({ theme }) => theme.FONT.B01};

  ${({ theme }) => theme.TYPOGRAPHY.B2_R};
`;

const StyledImg = styled.img`
  position: absolute;
  right: 0;
  bottom: 50%;

  width: 2rem;
  transform: translateY(50%);
  cursor: pointer;

  margin-right: 1rem;
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

  width: 14px;
  height: 14px;
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

const buttonCustomStyle = css`
  height: 4rem;
  padding: 1.1rem 0.8rem;

  font-size: 1.4rem;
`;
