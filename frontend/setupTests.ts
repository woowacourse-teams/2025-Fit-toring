// src/setupTests.ts
import '@testing-library/jest-dom/vitest';

import * as matchers from '@testing-library/jest-dom/matchers';
import { config } from 'dotenv';
import dotenv from 'dotenv';
import { expect, afterEach, beforeAll, afterAll } from 'vitest';
config();

let envFile = '.env.dev';

if (process.env.NODE_ENV === 'production') {
  envFile = '.env.prod';
} else if (process.env.NODE_ENV === 'test') {
  envFile = '.env.dev';
}

dotenv.config({ path: envFile });

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
