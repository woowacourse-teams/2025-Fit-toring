import userEvent from '@testing-library/user-event';
import { http, HttpResponse } from 'msw';
import { afterEach, beforeEach, describe, expect, it, vi } from 'vitest';

import { API_ENDPOINTS } from '../src/common/constants/apiEndpoints';
import { server } from '../src/common/mock/server';
import EditProfileForm from '../src/pages/editProfile/components/EditProfileForm';

import { render, screen, waitFor } from './utils';

const BASE_URL = process.env.API_BASE_URL;
const EDIT_PROFILE_URL = `${BASE_URL}${API_ENDPOINTS.MEMBERS_ME}`;

const myProfile = {
  name: '도기',
  gender: '남',
  phoneNumber: '010-5483-0455',
  image: null,
} as const;

const renderEditProfileForm = (image: string | null = null) => {
  return render(<EditProfileForm myProfile={{ ...myProfile, image }} />);
};

describe('EditProfileForm 프로필 이미지', () => {
  beforeEach(() => {
    Object.defineProperty(URL, 'createObjectURL', {
      writable: true,
      value: vi.fn(() => 'blob:profile-preview'),
    });
    Object.defineProperty(URL, 'revokeObjectURL', {
      writable: true,
      value: vi.fn(),
    });
    vi.spyOn(window, 'alert').mockImplementation(() => {});

    Object.defineProperty(globalThis, 'Image', {
      writable: true,
      value: class {
        onload: (() => void) | null = null;

        onerror: (() => void) | null = null;

        set src(value: string) {
          if (value === 'blob:broken-profile-preview') {
            this.onerror?.();
            return;
          }

          this.onload?.();
        }
      },
    });
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('기존 프로필 이미지가 없으면 삭제 버튼을 보여주지 않는다.', async () => {
    renderEditProfileForm();

    await userEvent.click(
      screen.getByRole('button', { name: '프로필 이미지 변경 메뉴 열기' }),
    );

    expect(screen.getByText('앨범에서 선택')).toBeInTheDocument();
    expect(screen.queryByText('프로필 사진 삭제')).not.toBeInTheDocument();
    expect(screen.getByText('닫기')).toBeInTheDocument();
  });

  it('서버에서 받은 프로필 이미지가 바뀌면 로컬 변경 전 미리보기에 반영한다.', () => {
    const { rerender } = renderEditProfileForm('https://example.com/old.jpg');

    rerender(
      <EditProfileForm
        myProfile={{ ...myProfile, image: 'https://example.com/new.jpg' }}
      />,
    );

    expect(screen.getByAltText('프로필 이미지')).toHaveAttribute(
      'src',
      'https://example.com/new.jpg',
    );
  });

  it('기존 프로필 이미지가 있으면 삭제 버튼을 보여준다.', async () => {
    renderEditProfileForm('https://example.com/profile.jpg');

    await userEvent.click(
      screen.getByRole('button', { name: '프로필 이미지 변경 메뉴 열기' }),
    );

    expect(screen.getByText('앨범에서 선택')).toBeInTheDocument();
    expect(screen.getByText('프로필 사진 삭제')).toBeInTheDocument();
    expect(screen.getByText('닫기')).toBeInTheDocument();
  });

  it('이미지를 선택하면 업로드된 key를 회원정보 수정 요청에 포함한다.', async () => {
    let requestBody: unknown;

    server.use(
      http.patch(EDIT_PROFILE_URL, async ({ request }) => {
        requestBody = await request.json();
        return new HttpResponse(null, { status: 204 });
      }),
    );

    const { container } = renderEditProfileForm();
    const fileInput = container.querySelector<HTMLInputElement>(
      'input[type="file"]',
    );
    const file = new File(['profile'], 'profile.jpg', {
      type: 'image/jpeg',
    });

    await userEvent.upload(fileInput as HTMLInputElement, file);

    await waitFor(() => {
      expect(
        screen.getByRole('button', { name: '회원정보 수정' }),
      ).not.toHaveStyle('pointer-events: none');
    });

    await userEvent.click(screen.getByRole('button', { name: '회원정보 수정' }));

    await waitFor(() => {
      expect(requestBody).toEqual({
        profileImageKey:
          'fit-toring/local/member-profile-image/default/mock-image.jpeg',
      });
    });
  });

  it('이미지를 2장 이상 선택하면 최대 1장 첨부 알림을 보여준다.', async () => {
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});

    const { container } = renderEditProfileForm();
    const fileInput = container.querySelector<HTMLInputElement>(
      'input[type="file"]',
    );
    const files = [
      new File(['profile-1'], 'profile-1.jpg', { type: 'image/jpeg' }),
      new File(['profile-2'], 'profile-2.jpg', { type: 'image/jpeg' }),
    ];

    await userEvent.upload(fileInput as HTMLInputElement, files);

    expect(alertSpy).toHaveBeenCalledWith(
      '이미지는 최대 1장까지 첨부할 수 있어요',
    );
    expect(screen.getByRole('button', { name: '회원정보 수정' })).toHaveStyle(
      'pointer-events: none',
    );
  });

  it('불러올 수 없는 이미지는 업로드하지 않고 알림을 보여준다.', async () => {
    const alertSpy = vi.spyOn(window, 'alert').mockImplementation(() => {});
    vi.spyOn(console, 'error').mockImplementation(() => {});
    vi.mocked(URL.createObjectURL).mockReturnValueOnce(
      'blob:broken-profile-preview',
    );

    const { container } = renderEditProfileForm();
    const fileInput = container.querySelector<HTMLInputElement>(
      'input[type="file"]',
    );
    const file = new File(['broken'], 'broken.jpg', {
      type: 'image/jpeg',
    });

    await userEvent.upload(fileInput as HTMLInputElement, file);

    expect(alertSpy).toHaveBeenCalledWith('이미지를 불러올 수 없어요');
    expect(screen.getByRole('button', { name: '회원정보 수정' })).toHaveStyle(
      'pointer-events: none',
    );
  });

  it('기존 프로필 이미지를 삭제하면 빈 profileImageKey를 회원정보 수정 요청에 포함한다.', async () => {
    let requestBody: unknown;

    server.use(
      http.patch(EDIT_PROFILE_URL, async ({ request }) => {
        requestBody = await request.json();
        return new HttpResponse(null, { status: 204 });
      }),
    );

    renderEditProfileForm('https://example.com/profile.jpg');

    await userEvent.click(
      screen.getByRole('button', { name: '프로필 이미지 변경 메뉴 열기' }),
    );
    await userEvent.click(screen.getByText('프로필 사진 삭제'));
    await userEvent.click(screen.getByRole('button', { name: '회원정보 수정' }));

    await waitFor(() => {
      expect(requestBody).toEqual({ profileImageKey: '' });
    });
  });
});
