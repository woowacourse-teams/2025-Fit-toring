// src/components/MyComponent.test.tsx
import { render, screen } from '@testing-library/react';
import MyComponent from '../src/MyComponent';
import { describe, expect, it } from 'vitest';

describe('MyComponent', () => {
  it('renders the correct text', () => {
    render(<MyComponent />);
    expect(screen.getByText('Hello Vitest!')).toBeInTheDocument();
  });
});
