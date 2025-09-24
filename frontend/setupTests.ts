// src/setupTests.ts
import '@testing-library/jest-dom/vitest';

import path from 'path';

import * as matchers from '@testing-library/jest-dom/matchers';
import { config } from 'dotenv';
import { expect, afterEach, beforeAll, afterAll } from 'vitest';

config({ path: path.resolve(process.cwd(), '.env.prod') });

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
