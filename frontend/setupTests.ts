// src/setupTests.ts
import '@testing-library/jest-dom/vitest';

import * as matchers from '@testing-library/jest-dom/matchers';
import { config } from 'dotenv';
import { expect, afterEach, beforeAll, afterAll } from 'vitest';
config();

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
