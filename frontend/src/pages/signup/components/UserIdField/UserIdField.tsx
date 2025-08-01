import React from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import Button from '../../../../common/components/Button/Button';
import FormField from '../../../../common/components/FormField/FormField';
import Input from '../../../../common/components/Input/Input';

function UserIdField() {
  return (
    <FormField label="아이디 *">
      <StyledInputAndBtnWrapper>
        <div className="input-wrapper">
          <Input id="id" placeholder="fittoring" />
        </div>
        <Button type="button" customStyle={buttonCustomStyle}>
          중복확인
        </Button>
      </StyledInputAndBtnWrapper>
    </FormField>
  );
}

export default UserIdField;

const StyledInputAndBtnWrapper = styled.div`
  display: flex;
  gap: 1.4rem;

  & > .input-wrapper {
    flex-grow: 1;
  }
`;

const buttonCustomStyle = css`
  height: 4rem;
  padding: 1.1rem 0.8rem;

  font-size: 1.4rem;
`;
