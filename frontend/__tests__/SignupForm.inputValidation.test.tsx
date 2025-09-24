import { ThemeProvider } from '@emotion/react';
import { screen, fireEvent, render } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';

import { THEME } from '../src/common/styles/theme';
import SignupForm from '../src/pages/signup/components/SignupForm/SignupForm';

const renderSignupForm = () => {
  render(
    <ThemeProvider theme={THEME}>
      <MemoryRouter>
        <SignupForm />
      </MemoryRouter>
    </ThemeProvider>,
  );
};

describe('SignUpForm', () => {
  describe('이름 검증', () => {
    it.each(['가나', '홍길동', '철수'])(
      '유효한 이름 "%s" 입력 시 에러가 발생하지 않는다.',
      async (name) => {
        // given
        renderSignupForm();
        const nameInput = screen.getByLabelText(/이름/i);

        // when
        await userEvent.clear(nameInput);
        await userEvent.type(nameInput, name);
        await userEvent.tab();

        // then
        expect(
          screen.queryByText(/이름은 2자 이상 5자 미만으로 입력해주세요./i),
        ).not.toBeInTheDocument();
        expect(nameInput).not.toHaveStyle(`border-color: ${THEME.FONT.ERROR}`);
      },
    );

    it.each(['가', '가나다라마바사'])(
      '잘못된 이름 "%s" 입력 시 에러 메시지가 출력된다.',
      async (name) => {
        // given
        renderSignupForm();
        const nameInput = screen.getByLabelText(/이름/i);

        // when
        await userEvent.clear(nameInput);
        await userEvent.type(nameInput, name);
        await userEvent.tab();

        // then
        expect(
          screen.getByText(/이름은 2자 이상 5자 미만으로 입력해주세요./i),
        ).toBeInTheDocument();
        expect(nameInput).toHaveStyle(`border-color: ${THEME.FONT.ERROR}`);
      },
    );
  });

  describe('아이디 입력 검증', () => {
    it.each([['abc12'], ['abcdefghijklmno']])(
      '아이디 "%s" 입력 시 길이 에러메시지가 출력되지 않는다.',
      async (id) => {
        // given
        renderSignupForm();
        const idInput = screen.getByLabelText(/아이디/i);

        // when
        await userEvent.clear(idInput);
        await userEvent.type(idInput, id);
        await userEvent.tab();

        // then
        expect(
          screen.queryByText(/아이디는 5자 이상 15자 미만으로 입력해주세요/i),
        ).not.toBeInTheDocument();
      },
    );

    it.each([['abc1'], ['abcdefghijklmnop']])(
      '아이디 "%s" 입력 시 길이 에러메시지가 출력된다.',
      async (id) => {
        // given
        renderSignupForm();
        const idInput = screen.getByLabelText(/아이디/i);

        // when
        await userEvent.clear(idInput);
        await userEvent.type(idInput, id);
        await userEvent.tab();

        // then
        expect(
          screen.getByText(/아이디는 5자 이상 15자 미만으로 입력해주세요/i),
        ).toBeInTheDocument();
      },
    );

    it.each([['abc!!'], ['가나다123'], ['test_123']])(
      '아이디 "%s" 입력 시 영문/숫자 외 문자는 허용되지 않는다.',
      async (id) => {
        // given
        renderSignupForm();
        const idInput = screen.getByLabelText(/아이디/i);

        // when
        await userEvent.clear(idInput);
        await userEvent.type(idInput, id);
        await userEvent.tab();

        // then
        expect(
          screen.getByText(/아이디는 영문과 숫자만 입력할 수 있습니다/i),
        ).toBeInTheDocument();
      },
    );
  });

  describe('비밀번호 검증', () => {
    it.each([['12345'], ['1234567890123456789']])(
      '비밀번호 "%s" 입력 시 에러메시지가 출력되지 않는다.',
      async (password) => {
        // given
        renderSignupForm();
        const passwordInput = screen.getByLabelText('비밀번호 *');

        // when
        await userEvent.type(passwordInput, password);

        // then
        const errorMessage = await screen.queryByText(
          /비밀번호는 5자 이상 20자 미만으로 입력해주세요/i,
        );
        expect(errorMessage).not.toBeInTheDocument();
      },
    );

    it.each([['1234'], ['123456789012345678900']])(
      '비밀번호 "%s" 입력 시 에러메시지가 출력된다.',
      async (password) => {
        // given
        renderSignupForm();
        const passwordInput = screen.getByLabelText('비밀번호 *');

        // when
        await userEvent.type(passwordInput, password);

        // then
        const errorMessage = await screen.findByText(
          /비밀번호는 5자 이상 20자 미만으로 입력해주세요/i,
        );
        expect(errorMessage).toBeInTheDocument();
      },
    );
  });

  describe('전화번호 입력', () => {
    it('유효한 전화번호 입력 시 인증요청 버튼이 활성화된다.', async () => {
      // given
      renderSignupForm();
      const codeInput = screen.getByLabelText(/전화번호/i);

      // when
      fireEvent.change(codeInput, { target: { value: '010-1234-5678' } });

      // then
      const button = screen.getByRole('button', { name: /인증요청/i });
      expect(button).toHaveStyle(`background-color: ${THEME.SYSTEM.MAIN600}`);
    });

    it('11자 미만 입력 시 인증요청 버튼이 비활성화된다.', async () => {
      // given
      renderSignupForm();
      const codeInput = screen.getByLabelText(/전화번호/i);

      // when
      fireEvent.change(codeInput, { target: { value: '010-1234-567' } });

      // then
      const button = screen.getByRole('button', { name: /인증요청/i });
      expect(button).toHaveStyle(`background-color: ${THEME.BG.GRAY}`);
    });
  });

  describe('인증번호 입력', () => {
    it('6자리 입력 시 인증하기 버튼이 활성화된다.', async () => {
      // given
      renderSignupForm();
      const codeInput = screen.getByLabelText(/인증번호/i);

      // when
      await userEvent.type(codeInput, '123456');

      // then
      const button = screen.getByRole('button', { name: /인증하기/i });
      expect(button).toHaveStyle(`background-color: ${THEME.SYSTEM.MAIN600}`);
    });

    it('6자 미만 입력 시 인증하기 버튼이 비활성화된다.', async () => {
      // given
      renderSignupForm();
      const codeInput = screen.getByLabelText(/인증번호/i);

      // when
      await userEvent.type(codeInput, '12345');

      // then
      const button = screen.getByRole('button', { name: /인증하기/i });
      expect(button).toHaveStyle(`background-color: ${THEME.BG.GRAY}`);
    });
  });
});
