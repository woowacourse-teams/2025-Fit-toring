import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';

import RecentSearchSection from '../src/pages/communitySearch/components/RecentSearchSection/RecentSearchSection';

import { render, screen } from './utils';

const renderRecentSearchSection = (onClear = vi.fn()) => {
  render(
    <RecentSearchSection
      keywords={['자바']}
      onKeywordClick={vi.fn()}
      onKeywordRemove={vi.fn()}
      onClear={onClear}
    />,
  );
};

describe('RecentSearchSection', () => {
  it('clears all keywords when clear confirm is accepted', async () => {
    const onClear = vi.fn();
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    renderRecentSearchSection(onClear);

    await userEvent.click(screen.getByRole('button', { name: '전체삭제' }));

    expect(window.confirm).toHaveBeenCalledWith(
      '최근 검색어를 모두 삭제하시겠습니까?',
    );
    expect(onClear).toHaveBeenCalledTimes(1);
  });

  it('keeps keywords when clear confirm is canceled', async () => {
    const onClear = vi.fn();
    vi.spyOn(window, 'confirm').mockReturnValue(false);
    renderRecentSearchSection(onClear);

    await userEvent.click(screen.getByRole('button', { name: '전체삭제' }));

    expect(onClear).not.toHaveBeenCalled();
  });
});
