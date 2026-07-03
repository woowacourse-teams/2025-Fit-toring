import userEvent from '@testing-library/user-event';
import { Route, Routes } from 'react-router-dom';
import { describe, expect, it } from 'vitest';

import CommunitySearchHeader from '../src/pages/communitySearch/components/CommunitySearchHeader/CommunitySearchHeader';

import { render, screen } from './utils';

describe('CommunitySearchHeader', () => {
  it('moves to community page when back button is clicked', async () => {
    render(
      <Routes>
        <Route path="/community/search" element={<CommunitySearchHeader />} />
        <Route path="/community" element={<div>커뮤니티 목록</div>} />
      </Routes>,
      {
        routerProps: {
          initialEntries: ['/community/search'],
        },
      },
    );

    await userEvent.click(
      screen.getByRole('button', { name: '뒤로가기 아이콘' }),
    );

    expect(screen.getByText('커뮤니티 목록')).toBeInTheDocument();
  });

  it('clears keyword when clear button is clicked', async () => {
    render(<CommunitySearchHeader defaultKeyword="검색어" />);

    await userEvent.click(
      screen.getByRole('button', { name: '검색어 지우기' }),
    );

    expect(screen.getByLabelText('커뮤니티 게시글 검색어')).toHaveValue('');
  });
});
