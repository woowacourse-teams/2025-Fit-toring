import path from 'path';

import { config } from 'dotenv';
import { defineConfig } from 'vitest/config';

config({ path: path.resolve(process.cwd(), '.env.test') });

export default defineConfig({
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: './setupTests.ts',
    css: true,
  },
});
