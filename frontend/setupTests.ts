// src/setupTests.ts
import '@testing-library/jest-dom/vitest';

import * as matchers from '@testing-library/jest-dom/matchers';
import { config } from 'dotenv';
import { expect, afterEach, beforeAll, afterAll } from 'vitest';

if (process.env.NODE_ENV === 'production') {
  config({ path: '.env.prod' });
} else if (process.env.NODE_ENV === 'test') {
  config({ path: '.env.dev' });
}

import { server } from './src/common/mock/server';

expect.extend(matchers);

beforeAll(() => {
  server.listen();
});
afterEach(() => {
  server.resetHandlers();
});
afterAll(() => {
  server.close();
});
